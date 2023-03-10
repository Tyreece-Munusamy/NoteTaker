package com.example.progressupdatedemo.domain.use_case.user

import android.util.Log
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.repository.UserRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User) {
        val createUserResponse = userRepository.createUser(user)
        if (createUserResponse is Response.Error) logCreateUserError(createUserResponse.message.toString())
    }

    private fun logCreateUserError(errorMessage: String) =
        Log.d("User Creation Error", "invoke: Failed to create user. Error: $errorMessage")
}