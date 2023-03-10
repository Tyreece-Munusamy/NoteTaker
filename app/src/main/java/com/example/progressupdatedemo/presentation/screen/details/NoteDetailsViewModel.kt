package com.example.progressupdatedemo.presentation.screen.details

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.use_case.note.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    private val _getNoteUseCaseResponseState = mutableStateOf<Response<Note>>(Response.Loading())
    val getNoteUseCaseResponseState = _getNoteUseCaseResponseState

    private val _isProcessingNoteDeletion = mutableStateOf(false)
    val isProcessingNoteDeletion = _isProcessingNoteDeletion

    fun getNoteByUUID(noteUUID: String) = viewModelScope.launch {
        val getNoteUseCaseResponse = invokeGetNoteUseCase(noteUUID)
        collectResponseFromGetNoteUseCase(getNoteUseCaseResponse)
    }

    fun toggleNoteFavouriteStatus(updatedNote: Note) = viewModelScope.launch {
        invokeUpdateNoteUseCase(updatedNote)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        _isProcessingNoteDeletion.value = true
        invokeDeleteNoteUseCase(note)
    }

    private suspend fun collectResponseFromGetNoteUseCase(getNoteUseCaseResponse: Flow<Response<Note>>) =
        getNoteUseCaseResponse.collect { _getNoteUseCaseResponseState.value = it }

    private fun invokeGetNoteUseCase(noteUUID: String) =
        noteUseCases.getNoteUseCase.invoke(noteUUID)

    private suspend fun invokeUpdateNoteUseCase(updatedNote: Note) =
        noteUseCases.updateNoteUseCase.invoke(updatedNote)

    private suspend fun invokeDeleteNoteUseCase(note: Note) =
        noteUseCases.deleteNoteUseCase.invoke(note)
}