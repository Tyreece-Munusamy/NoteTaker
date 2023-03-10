package com.example.progressupdatedemo.domain.use_case

import com.example.progressupdatedemo.data.repository.FakeAuthenticationRepository
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.use_case.authentication.SignIn
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SignInUseCaseTest {

    private lateinit var signIn: SignIn

    @Before
    fun setUp() {
        signIn =
            SignIn(FakeAuthenticationRepository())
    }

    @Test
    fun `Sign in user, successful sign in`() = runBlocking {
        var response: Response<Any> = Response.Success(false)
        signIn.invoke("shiven2@gmail.com", "shiven2").collect {
            response = it
        }
        assertThat(response == Response.Success(true)).isTrue()
    }

    @Test
    fun `Sign in user, unsuccessful sign in`() = runBlocking {
        var response: Response<Any> = Response.Success(true)
        signIn.invoke("shiven2@gmail.com", "shiven23").collect {
            response = it
        }
        assertThat(response == Response.Success(false)).isTrue()
    }
}