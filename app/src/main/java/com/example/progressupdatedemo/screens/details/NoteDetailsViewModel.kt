package com.example.progressupdatedemo.screens.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {
    val isProcessingNoteDeletion = mutableStateOf(false)

    fun toggleNoteFavouriteStatus(note: Note, updatedNote: Note) {
        viewModelScope.launch {
            repository.updateNoteIsFavouriteStatus(note, updatedNote)
        }
    }

    fun deleteNote(note: Note, onFailure: () -> Unit, onSuccess: () -> Unit) {
        isProcessingNoteDeletion.value = true
        viewModelScope.launch {
            val taskWithResult = repository.deleteNote(note)
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
                isProcessingNoteDeletion.value = false
            } else {
                onFailure.invoke()
                isProcessingNoteDeletion.value = false
            }
        }
    }
}