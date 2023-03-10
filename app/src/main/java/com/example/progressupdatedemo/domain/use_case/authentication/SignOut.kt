package com.example.progressupdatedemo.domain.use_case.authentication

import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import javax.inject.Inject

class SignOut @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke() = authRepository.signOut()
}