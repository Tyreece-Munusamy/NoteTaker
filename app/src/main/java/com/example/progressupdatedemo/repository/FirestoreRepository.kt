package com.example.progressupdatedemo.repository

import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    firebaseAuth: FirebaseAuth,
) {
    private val currentUser = firebaseAuth.currentUser
    private val userId = currentUser?.uid
    private val userEmail = currentUser?.email

    suspend fun getNotesByUserId(userId: String): DataOrException<NoteList, Boolean, Exception> {
        val dataOrException = DataOrException<NoteList, Boolean, Exception>()
        try {
            dataOrException.loading = true
            dataOrException.data =
                firestore.collection(Constants.COLLECTION_NAME_NOTES).whereEqualTo("userId", userId)
                    .get().await().toObjects(NoteList::class.java)[0]
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false
        } catch (exception: FirebaseFirestoreException) {
            dataOrException.exception = exception
        }
        return dataOrException
    }

    fun createUser(firstName: String, lastName: String) {
        val userDetailsMap = User(userId, firstName, lastName, userEmail).toMap()
        val noteList = NoteList(userId = userId, notes = emptyList())

        firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId.toString())
            .set(userDetailsMap)
        firestore.collection(Constants.COLLECTION_NAME_NOTES).document(userEmail.toString())
            .set(noteList)
    }

    fun addNote(note: Note): Task<Void> {
        return firestore.collection(Constants.COLLECTION_NAME_NOTES).document(userEmail.toString())
            .update("notes", FieldValue.arrayUnion(note))
    }

    fun deleteNote(note: Note): Task<Void> {
        return firestore.collection(Constants.COLLECTION_NAME_NOTES).document(userEmail.toString())
            .update("notes", FieldValue.arrayRemove(note))
    }

    fun updateNote(originalNote: Note, updatedNote: Note): Task<Void> {
        return addNote(updatedNote).addOnCompleteListener {
            deleteNote(originalNote)
        }
    }

    fun updateNoteIsFavouriteStatus(originalNote: Note, updatedNote: Note) {
        addNote(updatedNote)
        deleteNote(originalNote)
    }

    suspend fun getCurrentUser(): DataOrException<User, Boolean, Exception> {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val dataOrException = DataOrException<User, Boolean, Exception>()
        try {
            dataOrException.loading = true
            dataOrException.data =
                firestore.collection("users").whereEqualTo("email", currentUserEmail).get().await()
                    .toObjects(User::class.java)[0]
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false
        } catch (exception: FirebaseFirestoreException) {
            dataOrException.exception = exception
        }
        return dataOrException
    }

    fun updateUser(updateUser: User): Task<Void> {
        return firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId.toString())
            .set(updateUser.toMap())
    }
}