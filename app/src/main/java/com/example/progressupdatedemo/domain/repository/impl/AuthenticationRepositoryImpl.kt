package com.example.progressupdatedemo.domain.repository.impl

import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthenticationRepository {
    private var operationSuccessful = false

    override fun isUserAuthenticated() = firebaseAuth.currentUser != null

    override fun signUp(
        email: String,
        password: String,
    ): Flow<Response<Any>> = flow {
        try {
            emitLoadingResponse()
            awaitSignUpResult(email, password)
            emitResponseBasedOnOperationResult("Error signing up")
        } catch (e: Exception) {
            emitErrorResponse(e.localizedMessage ?: e.message.toString())
        }
    }

    override fun signIn(email: String, password: String): Flow<Response<Any>> = flow {
        operationSuccessful = false
        try {
            emitLoadingResponse()
            awaitSignInResult(email, password)
            emitResponseBasedOnOperationResult("Error signing in")
        } catch (e: Exception) {
            emitErrorResponse(e.localizedMessage ?: e.message.toString())
        }
    }

    override fun signOut(): Response<Any> {
        return try {
            firebaseAuth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(message = e.localizedMessage ?: e.message.toString())
        }
    }

    private suspend fun FlowCollector<Response<Any>>.emitLoadingResponse() =
        emit(Response.Loading())

    private suspend fun FlowCollector<Response<Any>>.emitSuccessResponse() =
        emit(Response.Success("Successful"))

    private suspend fun FlowCollector<Response<Any>>.emitErrorResponse(errorMessage: String) =
        emit(Response.Error(message = errorMessage))

    private suspend fun FlowCollector<Response<Any>>.emitResponseBasedOnOperationResult(errorMessage: String = "Error") =
        if (operationSuccessful) emitSuccessResponse() else emitErrorResponse(errorMessage)

    private suspend fun awaitSignUpResult(email: String, password: String) =
        signUpWithEmailAndPassword(email, password).addResultListener().await()

    private suspend fun awaitSignInResult(email: String, password: String) =
        signInWithEmailAndPassword(email, password).addResultListener().await()

    private fun signUpWithEmailAndPassword(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    private fun signInWithEmailAndPassword(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    private fun Task<AuthResult>.addResultListener(): Task<AuthResult> =
        this.addOnSuccessListener { operationSuccessful = true }
            .addOnFailureListener { operationSuccessful = false }
}