package com.example.progressupdatedemo.domain.use_cases

import com.example.progressupdatedemo.domain.authentication.AuthenticationRepository
import javax.inject.Inject

 class FirebaseSignUpUseCase @Inject constructor(private val authRepository: AuthenticationRepository) {
    operator fun invoke(email: String, password: String, firstName: String, lastName: String) =
        authRepository.signUpWithFirebase(email, password, firstName, lastName)
}