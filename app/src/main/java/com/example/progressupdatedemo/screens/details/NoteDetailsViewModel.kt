package com.example.progressupdatedemo.screens.details

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

    fun toggleNoteFavouriteStatus(note: Note, updatedNote: Note): Task<Void> {
        return repository.updateNote(note, updatedNote)
    }

    fun deleteNote(note: Note, onFailure: () -> Unit, onSuccess: () -> Unit) {
        repository.deleteNote(note).addOnCompleteListener {  task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        }
    }
}