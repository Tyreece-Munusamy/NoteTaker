package com.example.progressupdatedemo.domain.use_cases

import com.example.progressupdatedemo.data.repository.FakeAuthenticationRepository
import com.example.progressupdatedemo.utils.Response
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class FirebaseAuthenticationSignInUseCaseTest {

    private lateinit var firebaseAuthenticationSignInUseCase: FirebaseAuthenticationSignInUseCase

    @Before
    fun setUp() {
        firebaseAuthenticationSignInUseCase =
            FirebaseAuthenticationSignInUseCase(FakeAuthenticationRepository())
    }

    @Test
    fun `Sign in user, successful sign in`() = runBlocking {
        var response: Response<Boolean> = Response.Success(false)
        firebaseAuthenticationSignInUseCase.invoke("shiven2@gmail.com", "shiven2").collect {
            response = it
        }
        assertThat(response == Response.Success(true)).isTrue()
    }

    @Test
    fun `Sign in user, unsuccessful sign in`() = runBlocking {
        var response: Response<Boolean> = Response.Success(true)
        firebaseAuthenticationSignInUseCase.invoke("shiven2@gmail.com", "shiven23").collect {
            response = it
        }
        assertThat(response == Response.Success(false)).isTrue()
    }
}