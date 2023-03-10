package com.example.progressupdatedemo.domain.use_case.authentication

import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import javax.inject.Inject

class SignIn @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke(email: String, password: String) =
        authRepository.signIn(email, password)
}