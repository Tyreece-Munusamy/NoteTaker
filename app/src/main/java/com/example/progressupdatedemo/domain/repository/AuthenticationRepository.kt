package com.example.progressupdatedemo.domain.repository

import com.example.progressupdatedemo.data.Response
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun isUserAuthenticated(): Boolean

    fun signIn(email: String, password: String): Flow<Response<Any>>

    fun signOut(): Response<Any>

    fun signUp(email: String, password: String): Flow<Response<Any>>
}