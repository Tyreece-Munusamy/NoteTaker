package com.example.progressupdatedemo.domain.use_case.authentication

import com.example.progressupdatedemo.domain.repository.AuthenticationRepository
import javax.inject.Inject

class IsUserAuthenticated @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke() = authRepository.isUserAuthenticated()
}