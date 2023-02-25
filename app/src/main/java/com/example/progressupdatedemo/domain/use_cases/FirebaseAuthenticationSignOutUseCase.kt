package com.example.progressupdatedemo.domain.use_cases

import com.example.progressupdatedemo.domain.authentication.AuthenticationRepository
import javax.inject.Inject

class FirebaseAuthenticationSignOutUseCase @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke() = authRepository.signOutFromFirebaseAuth()
}