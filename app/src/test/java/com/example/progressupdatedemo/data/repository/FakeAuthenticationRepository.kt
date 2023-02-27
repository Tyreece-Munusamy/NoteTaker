package com.example.progressupdatedemo.data.repository

import com.example.progressupdatedemo.domain.authentication.AuthenticationRepository
import com.example.progressupdatedemo.utils.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mockito.Mockito

class FakeAuthenticationRepository : AuthenticationRepository {
    private var operationSuccessful: Boolean = false

    override fun isUserAuthenticated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun signInWithFirebaseAuth(email: String, password: String): Flow<Response<Boolean>> =
        flow {
            operationSuccessful = false
            try {
                emit(Response.Loading)
//                Mockito.`when`(firebaseAuthMock.signInWithEmailAndPassword(email, password)).thenReturn(true)
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

    override fun signOutFromFirebaseAuth(): Flow<Response<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun signUpWithFirebase(email: String, password: String): Flow<Response<Boolean>> {
        TODO("Not yet implemented")
    }

}