package com.example.progressupdatedemo.domain.use_case.authentication

data class AuthenticationUseCases(
    val isUserAuthenticated: IsUserAuthenticated,
    val signUp: SignUp,
    val signIn: SignIn,
    val signOut: SignOut
)