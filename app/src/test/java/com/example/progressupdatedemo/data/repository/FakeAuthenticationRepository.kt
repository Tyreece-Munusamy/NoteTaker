package com.example.progressupdatedemo.data.repository

import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthenticationRepository : AuthenticationRepository {

    override fun isUserAuthenticated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun signIn(email: String, password: String): Flow<Response<Any>> = flow { }

    override fun signOut(): Response<Any> {
        TODO("Not yet implemented")
    }

    override fun signUp(email: String, password: String): Flow<Response<Any>> {
        TODO("Not yet implemented")
    }

}