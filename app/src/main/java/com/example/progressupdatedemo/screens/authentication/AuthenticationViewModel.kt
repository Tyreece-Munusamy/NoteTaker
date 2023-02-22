package com.example.progressupdatedemo.screens.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.example.progressupdatedemo.utils.isValid
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    val isProcessingAuthenticationRequest = mutableStateOf(false)

    fun signInUser(
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        isProcessingAuthenticationRequest.value = true
        signInUserWithEmailAndPassword(email, password, onFailure, onSuccess)
    }

    fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        isProcessingAuthenticationRequest.value = true
        createUserWithEmailAndPassword(firstName, lastName, email, password, onFailure, onSuccess)
    }

    fun validateLoginDetails(email: String, password: String): Boolean {
        return email.isValid() && password.isValid()
    }

    fun validateSignUpDetails(firstName: String, lastName: String, email: String, password: String): Boolean {
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

    private fun createUserWithEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val taskWithAuthResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
        val persistCreatedUserToFirestoreOnSuccess = {
            onSuccess.invoke()
            repository.createUser(firstName, lastName)
        }
        invokeOnSuccessOrOnFailureOnTaskCompletion(
            taskWithAuthResult, persistCreatedUserToFirestoreOnSuccess, onFailure
        )
    }

    private fun invokeOnSuccessOrOnFailureOnTaskCompletion(
        taskWithAuthResult: Task<AuthResult>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        taskWithAuthResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
                isProcessingAuthenticationRequest.value = false
            } else {
                onFailure.invoke()
                isProcessingAuthenticationRequest.value = false
            }
        }
    }
}