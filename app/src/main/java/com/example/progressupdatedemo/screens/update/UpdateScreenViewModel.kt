package com.example.progressupdatedemo.screens.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateScreenViewModel @Inject constructor(private val repository: FirestoreRepository) :
    ViewModel() {

    fun updateNote(
        originalNote: Note,
        updatedNote: Note,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val task = repository.updateNote(originalNote, updatedNote)
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
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
        val updatedUser = User(id = id, firstName = firstName, lastName = lastName, email = email)
        repository.updateUser(updatedUser).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        }
    }
}