package com.example.progressupdatedemo.screens.authentication

import androidx.lifecycle.ViewModel
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signInUserWithEmailAndPassword(
        email: String,
        password: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val taskWithAuthResult = firebaseAuth.signInWithEmailAndPassword(email, password)
        invokeOnSuccessOrOnFailureOnTaskCompletion(taskWithAuthResult, onSuccess, onFailure)
    }

    fun createUserWithEmailAndPassword(
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
            } else {
                onFailure.invoke()
            }
        }
    }
}