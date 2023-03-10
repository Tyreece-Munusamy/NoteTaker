package com.example.progressupdatedemo.domain.repository.impl

import com.example.progressupdatedemo.core.constants.Constants
import com.example.progressupdatedemo.core.utils.buildErrorResponse
import com.example.progressupdatedemo.core.utils.buildSuccessResponse
import com.example.progressupdatedemo.core.utils.getDocumentSnapshotListener
import com.example.progressupdatedemo.core.utils.removeSnapshotListenerOnClose
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {
    private var operationSuccessful = false

    override fun getUser(): Flow<Response<User>> = callbackFlow {
        val userDocumentReference = getUserDocumentReference()
        val snapshotListener = getDocumentSnapshotListener(userDocumentReference, User::class.java)
        removeSnapshotListenerOnClose(snapshotListener)
    }

    override suspend fun createUser(user: User): Response<Any> {
        setUserEmailAndUID(user)
        operationSuccessful = false
        val operation: suspend () -> Unit = { awaitUserCreation(user) }
        val errorMessage = "Error creating user"
        return buildResponseBasedOnOperationResult(operation, errorMessage)
    }

    override suspend fun updateUser(updateUser: User): Response<Any> {
        operationSuccessful = false
        val operation: suspend () -> Unit = { awaitUserUpdateResult(updateUser) }
        val errorMessage = "Error updating user"
        return buildResponseBasedOnOperationResult(operation, errorMessage)
    }

    private fun setUserEmailAndUID(user: User) {
        user.uid = firebaseAuth.currentUser?.uid
        user.email = firebaseAuth.currentUser?.email
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

    private suspend fun awaitUserCreation(user: User) =
        setUserDocument(user).addResultListener().await()

    private suspend fun awaitUserUpdateResult(updateUser: User) =
        setUserDocument(updateUser).addResultListener().await()

    private fun setUserDocument(user: User) = getUserDocumentReference().set(user)

    private fun Task<Void>.addResultListener(): Task<Void> =
        this.addOnSuccessListener { operationSuccessful = true }
            .addOnFailureListener { operationSuccessful = false }

    private fun getUserDocumentReference(): DocumentReference {
        val userId = firebaseAuth.currentUser?.uid
        return firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId.toString())
    }
}
