package com.example.progressupdatedemo.screens.update

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.domain.models.Note
import com.example.progressupdatedemo.domain.models.User
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateScreenViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {
    val isProcessingUpdateRequest = mutableStateOf(false)

    fun updateNote(
        originalNote: Note,
        updatedNote: Note,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val taskWithResult = repository.updateNote(originalNote, updatedNote)
            isProcessingUpdateRequest.value = true
            invokeOnSuccessOrOnFailureOnTaskCompletion(taskWithResult, onSuccess, onFailure)
        }
    }

    fun updateUser(
        id: String,
        firstName: String,
        lastName: String,
        email: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val updatedUser =
                User(id = id, firstName = firstName, lastName = lastName, email = email)
            val taskWithResult = repository.updateUser(updatedUser)
            isProcessingUpdateRequest.value = true
            invokeOnSuccessOrOnFailureOnTaskCompletion(taskWithResult, onSuccess, onFailure)
        }
    }

    private fun invokeOnSuccessOrOnFailureOnTaskCompletion(
        taskWithResult: Task<Void>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        taskWithResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
                isProcessingUpdateRequest.value = false
            } else {
                onFailure.invoke()
                isProcessingUpdateRequest.value = false
            }
        }
    }
}