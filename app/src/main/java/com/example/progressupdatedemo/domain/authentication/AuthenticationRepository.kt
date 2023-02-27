package com.example.progressupdatedemo.domain.authentication

import com.example.progressupdatedemo.utils.Response
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    fun isUserAuthenticated(): Boolean

    fun signInWithFirebaseAuth(email: String, password: String): Flow<Response<Boolean>>

    fun signOutFromFirebaseAuth(): Flow<Response<Boolean>>

    fun signUpWithFirebase(
        email: String,
        password: String,
    ): Flow<Response<Boolean>>
}