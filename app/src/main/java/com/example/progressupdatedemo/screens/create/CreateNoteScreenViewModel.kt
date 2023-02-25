package com.example.progressupdatedemo.screens.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.domain.models.Note
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteScreenViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {
    val isProcessingNoteCreation = mutableStateOf(false)

    fun createNote(note: Note, onFailure: () -> Unit, onSuccess: () -> Unit) {
        isProcessingNoteCreation.value = true
        viewModelScope.launch {
            addNote(note, onSuccess, onFailure)
        }
    }

    fun validateNoteDetails(title: String, message: String): Boolean {
        return title.trim().isNotEmpty() && message.trim().isNotEmpty()
    }

    private fun addNote(
        note: Note,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val taskWithResult = repository.addNote(note)
        invokeOnSuccessOrOnFailureOnTaskCompletion(taskWithResult, onSuccess, onFailure)
    }

    private fun invokeOnSuccessOrOnFailureOnTaskCompletion(
        taskWithResult: Task<Void>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        taskWithResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
                isProcessingNoteCreation.value = false
            } else {
                onFailure.invoke()
                isProcessingNoteCreation.value = false
            }
        }
    }
}