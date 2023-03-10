package com.example.progressupdatedemo.domain.use_case.user

import android.util.Log
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(updateUser: User) {
        val updateUserResponse = userRepository.updateUser(updateUser)
        if (updateUserResponse is Response.Error) logUpdateUserError(updateUserResponse.message.toString())
    }

    private fun logUpdateUserError(errorMessage: String) =
        Log.d("User Update Error", "invoke: Failed to update user. Error: $errorMessage")
}