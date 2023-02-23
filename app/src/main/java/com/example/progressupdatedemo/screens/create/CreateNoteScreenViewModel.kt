package com.example.progressupdatedemo.screens.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNoteScreenViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {
    val isProcessingNoteCreation = mutableStateOf(false)

    fun createNote(note: Note, onFailure: () -> Unit, onSuccess: () -> Unit) {
        isProcessingNoteCreation.value = true
        repository.addNote(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
                isProcessingNoteCreation.value = false
            } else {
                onFailure.invoke()
                isProcessingNoteCreation.value = false
            }
        }
    }

    fun validateNoteDetails(title: String, message: String): Boolean {
        return title.trim().isNotEmpty() && message.trim().isNotEmpty()
    }
}