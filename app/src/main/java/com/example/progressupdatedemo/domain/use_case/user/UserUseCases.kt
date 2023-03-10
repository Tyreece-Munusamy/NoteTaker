package com.example.progressupdatedemo.domain.use_case.user

data class UserUseCases(
    val createUserUseCase: CreateUserUseCase,
    val getUserUseCase: GetUserUseCase,
    val updateUserUseCase: UpdateUserUseCase
)