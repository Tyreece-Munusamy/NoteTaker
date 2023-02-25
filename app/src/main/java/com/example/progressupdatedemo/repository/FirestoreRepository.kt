package com.example.progressupdatedemo.repository

import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.domain.models.Note
import com.example.progressupdatedemo.domain.models.NoteList
import com.example.progressupdatedemo.domain.models.User
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
    private val firebaseAuth: FirebaseAuth,
) {
    suspend fun getNotesByUserId(userId: String): DataOrException<NoteList, Boolean, Exception> {
        val dataOrException = getDataOrExceptionFromApiCall {
            firestore.collection(Constants.COLLECTION_NAME_NOTES).whereEqualTo("userId", userId)
                .get().await().toObjects(NoteList::class.java)[0]
        }
        return dataOrException
    }

    suspend fun createUser(firstName: String, lastName: String) {
        val userId = firebaseAuth.currentUser?.uid
        val userEmail = firebaseAuth.currentUser?.email
        val userDetailsMap = User(userId, firstName, lastName, userEmail).toMap()
        val noteList = NoteList(userId = userId, notes = emptyList())

        firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId.toString())
            .set(userDetailsMap).await()
        firestore.collection(Constants.COLLECTION_NAME_NOTES).document(userEmail.toString())
            .set(noteList).await()
    }

    fun addNote(note: Note): Task<Void> {
        val userEmail = firebaseAuth.currentUser?.email
        return firestore.collection(Constants.COLLECTION_NAME_NOTES).document(userEmail.toString())
            .update("notes", FieldValue.arrayUnion(note))
    }

    fun deleteNote(note: Note): Task<Void> {
        val userEmail = firebaseAuth.currentUser?.email
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
        val userEmail = firebaseAuth.currentUser?.email
        val dataOrException = getDataOrExceptionFromApiCall {
            firestore.collection(Constants.COLLECTION_NAME_USERS).whereEqualTo("email", userEmail)
                .get().await().toObjects(User::class.java)[0]
        }
        return dataOrException
    }

    fun updateUser(updateUser: User): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        return firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId.toString())
            .set(updateUser.toMap())
    }

    private suspend fun <T> getDataOrExceptionFromApiCall(apiCall: suspend () -> T): DataOrException<T, Boolean, Exception> {
        val dataOrException = DataOrException<T, Boolean, Exception>()
        try {
            dataOrException.loading = true
            dataOrException.data = apiCall.invoke()
            if (dataOrException.data != null) dataOrException.loading = false
        } catch (exception: FirebaseFirestoreException) {
            dataOrException.exception = exception
        }
        return dataOrException
    }
}