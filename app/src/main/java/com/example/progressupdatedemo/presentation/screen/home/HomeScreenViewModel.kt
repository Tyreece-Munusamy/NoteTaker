package com.example.progressupdatedemo.presentation.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.core.utils.dataIsNotNullOrEmpty
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.model.User
import com.example.progressupdatedemo.domain.use_case.authentication.AuthenticationUseCases
import com.example.progressupdatedemo.domain.use_case.note.NoteUseCases
import com.example.progressupdatedemo.domain.use_case.user.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases,
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    private val _getNotesUseCaseResponseState =
        mutableStateOf<Response<List<Note>>>(Response.Loading())
    val getNotesUseCaseResponseState = _getNotesUseCaseResponseState

    private val _getUserUseCaseResponseState = mutableStateOf<Response<User>>(Response.Loading())
    val getUserUseCaseResponseState = _getUserUseCaseResponseState

    init {
        getNotesSortedByTimestamp()
        getCurrentUser()
    }

    private fun getNotesSortedByTimestamp() = viewModelScope.launch {
        val getNotesUseCaseResponse = invokeGetNotesUseCase()
        collectThenSortGetNotesUseCaseResponse(getNotesUseCaseResponse)
    }

    private fun getCurrentUser() = viewModelScope.launch {
        val getUserUseCaseResponse = invokeGetUserUseCase()
        collectGetUserUseCaseResponse(getUserUseCaseResponse)
    }

    fun signOut() = viewModelScope.launch { invokeSignOutUseCase() }

    private fun invokeGetNotesUseCase() = noteUseCases.getNotesUseCase.invoke()

    private suspend fun collectThenSortGetNotesUseCaseResponse(getNotesUseCaseResponse: Flow<Response<List<Note>>>) =
        getNotesUseCaseResponse.collect {
            _getNotesUseCaseResponseState.value = it
            if (_getNotesUseCaseResponseState.value.dataIsNotNullOrEmpty()) sortNotesByTimestamp()
        }

    private fun sortNotesByTimestamp() {
        val sortedNotes = sortNotesByCreationDate(_getNotesUseCaseResponseState.value.data!!)
        assignResultToState(sortedNotes)
    }

    private fun sortNotesByCreationDate(notes: List<Note>) =
        notes.toMutableList().sortedWith(compareBy { it.creationDate }).asReversed()

    private fun assignResultToState(sortedList: List<Note>) {
        _getNotesUseCaseResponseState.value = Response.Success(sortedList)
    }

    private fun invokeGetUserUseCase() = userUseCases.getUserUseCase.invoke()

    private suspend fun collectGetUserUseCaseResponse(getUserUseCaseResponse: Flow<Response<User>>) =
        getUserUseCaseResponse.collect { _getUserUseCaseResponseState.value = it }

    private fun invokeSignOutUseCase() = authenticationUseCases.signOut.invoke()
}