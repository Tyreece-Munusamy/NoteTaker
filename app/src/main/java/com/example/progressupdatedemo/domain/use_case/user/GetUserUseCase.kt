package com.example.progressupdatedemo.domain.use_case.user

import com.example.progressupdatedemo.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke() = userRepository.getUser()
}