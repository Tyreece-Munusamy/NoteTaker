package com.example.progressupdatedemo.screens.create

import androidx.lifecycle.ViewModel
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNoteScreenViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {

    fun createNote(note: Note, onFailure: () -> Unit, onSuccess: () -> Unit) {
        repository.addNote(note).addOnCompleteListener {  task ->
            if (task.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        }
    }
}