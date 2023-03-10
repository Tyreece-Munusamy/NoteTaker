package com.example.progressupdatedemo.domain.repository

import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: User): Response<Any>

    fun getUser(): Flow<Response<User>>

    suspend fun updateUser(updateUser: User): Response<Any>
}