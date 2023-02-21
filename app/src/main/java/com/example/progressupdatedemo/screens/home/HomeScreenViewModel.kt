package com.example.progressupdatedemo.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.data.DataOrException
import com.example.progressupdatedemo.models.NoteList
import com.example.progressupdatedemo.models.User
import com.example.progressupdatedemo.repository.FirestoreRepository
import com.example.progressupdatedemo.utils.toJson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FirestoreRepository): ViewModel() {
    val notes = mutableStateOf(DataOrException(NoteList() , false, Exception("")))
    val user = mutableStateOf(DataOrException(User(), false, Exception("")))

    init {
        viewModelScope.launch {
            getAllNotesByUserId()
            getCurrentUser()
        }
    }

    private suspend fun getAllNotesByUserId() {
        notes.value.loading = true
        notes.value = repository.getNotesByUserId(FirebaseAuth.getInstance().currentUser?.uid!!)
        if (notes.value.data.toString().isNotEmpty()) {
            val notesOrderedByTimestamp = notes.value.data?.notes?.toMutableList()?.sortedWith(compareBy { it.creationDate })?.reversed()
            notes.value.data?.notes = notesOrderedByTimestamp
            notes.value.loading = false
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private suspend fun getCurrentUser() {
        user.value.loading = true
        user.value = repository.getCurrentUser()
        if (user.value.data.toString().isNotEmpty()) user.value.loading = false
    }
}