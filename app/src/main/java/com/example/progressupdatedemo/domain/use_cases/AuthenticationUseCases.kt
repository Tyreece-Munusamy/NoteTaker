package com.example.progressupdatedemo.domain.use_cases

data class AuthenticationUseCases(
    val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    val firebaseSignUpUseCase: FirebaseSignUpUseCase,
    val firebaseAuthenticationSignInUseCase: FirebaseAuthenticationSignInUseCase,
    val firebaseAuthenticationSignOutUseCase: FirebaseAuthenticationSignOutUseCase
)