package com.example.progressupdatedemo.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.Note
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    val notes = mutableStateOf(DataOrException(NoteList(), false, Exception("")))
    val user = mutableStateOf(DataOrException(User(), false, Exception("")))
    val isLoadingNotesAndUserData = mutableStateOf(false)

    init {
        viewModelScope.launch {
            isLoadingNotesAndUserData.value = true
            getAllNotesByUserId()
            getCurrentUser()
        }.invokeOnCompletion {
            isLoadingNotesAndUserData.value = false
        }
    }

    private suspend fun getAllNotesByUserId() {
//        notes.value.loading = true
        notes.value = repository.getNotesByUserId(firebaseAuth.currentUser?.uid!!)
        if (notes.value.data.toString().isNotEmpty()) {
            val notesOrderedByTimestamp = orderListNotesByCreationDate(notes.value.data?.notes!!)
            notes.value.data?.notes = notesOrderedByTimestamp
//            notes.value.loading = false
        }
    }

    fun signOut() = viewModelScope.launch { firebaseAuth.signOut() }

    private suspend fun getCurrentUser() {
//        user.value.loading = true
        user.value = repository.getCurrentUser()
//        if (user.value.data.toString().isNotEmpty()) user.value.loading = false
    }

    private fun orderListNotesByCreationDate(notes: List<Note>): List<Note> {
        return notes.toMutableList().sortedWith(compareBy { it.creationDate }).asReversed()
    }
}