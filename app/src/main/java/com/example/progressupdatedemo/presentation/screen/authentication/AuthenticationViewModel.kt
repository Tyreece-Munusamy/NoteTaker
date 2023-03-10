package com.example.progressupdatedemo.presentation.screen.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.core.utils.isValid
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.use_case.authentication.AuthenticationUseCases
import com.example.progressupdatedemo.domain.use_case.user.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases,
    private val userUseCases: UserUseCases,
) : ViewModel() {

    private val _isProcessingAuthenticationRequest = mutableStateOf(false)
    val isProcessingAuthenticationRequest = _isProcessingAuthenticationRequest

    val isUserAuthenticated get() = authenticationUseCases.isUserAuthenticated.invoke()

    private val _signUpUseCaseResponseState = mutableStateOf<Response<Any>>(Response.Success(false))
    private val _signInUseCaseResponseState = mutableStateOf<Response<Any>>(Response.Success(false))

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _isProcessingAuthenticationRequest.value = true
            signInWithEmailAndPassword(email, password, onSuccess, onFailure)
        }
    }

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            _isProcessingAuthenticationRequest.value = true
            signUpWithEmailAndPassword(email, password, firstName, lastName, onSuccess, onFailure)
        }
    }

    fun validateSignUpDetails(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Boolean {
        return firstName.isValid() && lastName.isValid() && email.isValid() && password.isValid()
    }

    fun validateLoginDetails(email: String, password: String): Boolean {
        return email.isValid() && password.isValid()
    }

    private suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val signInUseCaseResponse = invokeSignInUseCase(email, password)
        collectThenProcessSignInUseCaseResponse(
            signInUseCaseResponse, onSuccess, onFailure
        )
    }

    private fun invokeSignInUseCase(
        email: String,
        password: String,
    ) = authenticationUseCases.signIn.invoke(email, password)

    private suspend fun collectThenProcessSignInUseCaseResponse(
        signInUseCaseResponse: Flow<Response<Any>>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        signInUseCaseResponse.collect {
            _signInUseCaseResponseState.value = it
            processSignInUseCaseResponse(_signInUseCaseResponseState.value, onSuccess, onFailure)
        }
    }

    private fun processSignInUseCaseResponse(
        signInUseCaseResponse: Response<Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        when (signInUseCaseResponse) {
            is Response.Success -> {
                onSuccess.invoke()
                _isProcessingAuthenticationRequest.value = false
            }
            is Response.Error -> {
                onFailure.invoke(signInUseCaseResponse.message ?: "Error")
                _isProcessingAuthenticationRequest.value = false
            }
            else -> isProcessingAuthenticationRequest.value = true
        }
    }

    private suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val signUpUseCaseResponse = invokeSignUpUseCase(email, password)
        collectThenProcessSignUpUseCaseResponse(
            signUpUseCaseResponse,
            firstName,
            lastName,
            onSuccess,
            onFailure
        )
    }

    private fun invokeSignUpUseCase(
        email: String,
        password: String,
    ) = authenticationUseCases.signUp.invoke(email, password)

    private suspend fun collectThenProcessSignUpUseCaseResponse(
        signUpUseCaseResponse: Flow<Response<Any>>,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        signUpUseCaseResponse.collect {
            _signUpUseCaseResponseState.value = it
            processSignUpUseCaseResponse(
                _signUpUseCaseResponseState.value, firstName, lastName, onSuccess, onFailure
            )
        }
    }

    private suspend fun processSignUpUseCaseResponse(
        useCaseState: Response<Any>,
        firstName: String, lastName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        when (useCaseState) {
            is Response.Success -> {
                persistUserToFirestore(firstName, lastName)
                onSuccess.invoke()
                _isProcessingAuthenticationRequest.value = false
            }
            is Response.Error -> {
                onFailure.invoke(useCaseState.message ?: "Error")
                _isProcessingAuthenticationRequest.value = false
            }
            else -> isProcessingAuthenticationRequest.value = true
        }
    }

    private suspend fun persistUserToFirestore(firstName: String, lastName: String) =
        invokeCreateUserUseCase(User(firstName = firstName, lastName = lastName))

    private suspend fun invokeCreateUserUseCase(user: User) =
        userUseCases.createUserUseCase.invoke(user)
}