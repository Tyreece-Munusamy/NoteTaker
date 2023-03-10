package com.example.progressupdatedemo.domain.repository.impl

import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.core.utils.*
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.repository.NoteRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : NoteRepository {
    private var operationSuccessful = false

//    override fun getNotes(noteUUID: String): Flow<Response<List<Note>>> = callbackFlow{
//        val userUID = firebaseAuth.currentUser?.uid
//        Response.Loading
//        val snapshotListener = firebaseFirestore.collection(Constants.COLLECTION_NAME_USERS)
//            .document(userUID.toString()).collection(Constants.COLLECTION_NAME_NOTES).document(noteUUID)
//            .addSnapshotListener{ snapshot, exception ->
//                val response = if(snapshot!=null){
//                    val notes = snapshot.toObjects(Note::class.java)
//                    Response.Success<List<Note>>(notes)
//                }
//                else{
//                    Response.Error(exception?.message ?: exception.toString())
//                }
//                trySend(response).isSuccess
//            }
//        awaitClose {
//            snapshotListener.remove()
//        }
//    }

    override fun getNotes(): Flow<Response<List<Note>>> = callbackFlow {
        delay(2000L)
        val noteCollectionReference = getNoteCollectionReference()
        val snapshotListener = getCollectionSnapshotListener(noteCollectionReference, Note::class.java)
        removeSnapshotListenerOnClose(snapshotListener)
    }

    override fun getNote(noteUUID: String): Flow<Response<Note>> = callbackFlow {
        val noteDocumentReference = getNoteDocumentReference(noteUUID)
        val snapshotListener = getDocumentSnapshotListener(noteDocumentReference, Note::class.java)
        removeSnapshotListenerOnClose(snapshotListener)
    }

    override suspend fun createNote(note: Note): Response<Any> {
        val operation: suspend () -> Unit = { awaitNoteCreationResult(note) }
        val errorMessage = "Error creating note"
        return buildResponseBasedOnOperationResult(operation, errorMessage)
    }

    override suspend fun deleteNote(note: Note): Response<Any> {
        val operation: suspend () -> Unit = { awaitNoteDeletionResult(note) }
        val errorMessage = "Error deleting note"
        return buildResponseBasedOnOperationResult(operation, errorMessage)
    }

    override suspend fun updateNote(updatedNote: Note): Response<Any> {
        val operation: suspend () -> Unit = { awaitNoteUpdateResult(updatedNote) }
        val errorMessage = "Error updating note"
        return buildResponseBasedOnOperationResult(operation, errorMessage)
    }

    private suspend fun buildResponseBasedOnOperationResult(
        operation: suspend () -> Unit,
        errorMessage: String = "Error",
    ): Response<Any> = try {
        operationSuccessful = false
        operation.invoke()
        if (operationSuccessful) buildSuccessResponse(this.operationSuccessful)
        else buildErrorResponse(errorMessage)
    } catch (e: Exception) {
        buildErrorResponse(e.localizedMessage ?: e.message.toString())
    }

    private suspend fun awaitNoteCreationResult(note: Note) {
        setNote(note).addResultListener().await()
    }

    private suspend fun awaitNoteDeletionResult(note: Note) {
        deleteNoteDocument(note).addResultListener().await()
    }

    private fun deleteNoteDocument(note: Note) = getNoteDocumentReference(note.uuid).delete()

    private suspend fun awaitNoteUpdateResult(note: Note) = setNote(note).addResultListener().await()

    private fun setNote(note: Note) = getNoteDocumentReference(note.uuid).set(note)

    private fun getNoteDocumentReference(noteUUID: String) =
        getNoteCollectionReference().document(noteUUID)

    private fun getNoteCollectionReference(): CollectionReference {
        val userUID = getCurrentUserUID()
        return firestore.collection(Constants.COLLECTION_NAME_USERS).document(userUID.toString())
            .collection(Constants.COLLECTION_NAME_NOTES)
    }

    private fun Task<Void>.addResultListener(): Task<Void> =
        this.addOnSuccessListener { operationSuccessful = true }
            .addOnFailureListener { operationSuccessful = false }

    private fun getCurrentUserUID() = firebaseAuth.currentUser?.uid
}