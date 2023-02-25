package com.example.progressupdatedemo.screens.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.domain.use_cases.AuthenticationUseCases
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.example.progressupdatedemo.utils.Response
import com.example.progressupdatedemo.utils.isValid
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth,
    private val authenticationUseCases: AuthenticationUseCases,
) : ViewModel() {

    val isProcessingAuthenticationRequest = mutableStateOf(false)
    val isUserAuthenticated get() = authenticationUseCases.isUserAuthenticatedUseCase()

    private val _signUpState = mutableStateOf<Response<Boolean>>(Response.Success(false))

    fun signIn(
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            isProcessingAuthenticationRequest.value = true
            signInUserWithEmailAndPassword(email, password, onFailure, onSuccess)
        }
    }

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            isProcessingAuthenticationRequest.value = true
            signUpUseCase(email, password, firstName, lastName, onSuccess, onFailure)
        }
    }

    private suspend fun signUpUseCase(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val firebaseSignUpUseCase = authenticationUseCases.firebaseSignUpUseCase.invoke(
            email, password, firstName, lastName
        )

        firebaseSignUpUseCase.collect {
            _signUpState.value = it
            if (_signUpState.value != Response.Loading) {
                invokeOnSuccessOrFailureOnSignUpUseCaseCompletion(
                    firstName, lastName, onSuccess, onFailure
                )
            }
        }
    }

    private fun invokeOnSuccessOrFailureOnSignUpUseCaseCompletion(
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        if (_signUpState.value == Response.Success(true)) {
            persistUserToFirestore(firstName, lastName, onSuccess)
        } else {
            onFailure.invoke()
            isProcessingAuthenticationRequest.value = false
        }
    }

    private fun persistUserToFirestore(firstName: String, lastName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.createUser(
                firstName, lastName
            )
        }.invokeOnCompletion {
            onSuccess.invoke()
            isProcessingAuthenticationRequest.value = false
        }
    }

    fun validateLoginDetails(email: String, password: String): Boolean {
        return email.isValid() && password.isValid()
    }

    fun validateSignUpDetails(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Boolean {
        return firstName.isValid() && lastName.isValid() && email.isValid() && password.isValid()
    }

    private fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val taskWithAuthResult = firebaseAuth.signInWithEmailAndPassword(email, password)
        invokeOnSuccessOrOnFailureOnTaskCompletion(taskWithAuthResult, onSuccess, onFailure)
    }

    private fun invokeOnSuccessOrOnFailureOnTaskCompletion(
        taskWithAuthResult: Task<AuthResult>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        taskWithAuthResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
                _signUpState.value = Response.Success(false)
                isProcessingAuthenticationRequest.value = false
            } else {
                onFailure.invoke()
                isProcessingAuthenticationRequest.value = false
            }
        }
    }
}