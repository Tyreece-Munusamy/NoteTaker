package com.example.progressupdatedemo.data

import android.util.Log
import com.example.progressupdatedemo.domain.authentication.AuthenticationRepository
import com.example.progressupdatedemo.utils.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthenticationRepository {
    private var operationSuccessful: Boolean = false

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun signInWithFirebaseAuth(email: String, password: String): Flow<Response<Boolean>> =
        flow {
            operationSuccessful = false
            try {
                emit(Response.Loading)
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    operationSuccessful = true
                }.await()
                emit(Response.Success(operationSuccessful))
            } catch (e: Exception) {
                emit(
                    Response.Error(
                        e.localizedMessage
                            ?: "An unexpected error has occurred while trying to sign in"
                    )
                )
            }
        }

    override fun signOutFromFirebaseAuth(): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            firebaseAuth.signOut()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(
                Response.Error(
                    e.localizedMessage
                        ?: "An unexpected error has occurred while trying to sign out"
                )
            )
        }
    }

    override fun signUpWithFirebase(
        email: String,
        password: String,
    ): Flow<Response<Boolean>> = flow {
        operationSuccessful = false
        try {
            emit(Response.Loading)
            val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val a = task.addOnSuccessListener {
                Log.d("TAG", "signUpWithFirebase: $it")
                operationSuccessful = true
            }.addOnFailureListener {
                operationSuccessful = false
            }.await()
            Log.d("TAG", "signUpWithFirebase: $a")
            emit(Response.Success(operationSuccessful))
        } catch (e: Exception) {
            emit(
                Response.Error(
                    e.localizedMessage
                        ?: "An unexpected error has occurred while trying to sign out"
                )
            )
        }
    }
}