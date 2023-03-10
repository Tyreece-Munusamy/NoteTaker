package com.example.progressupdatedemo.presentation.screen.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.use_case.note.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteScreenViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    private val _isProcessingNoteCreation = mutableStateOf(false)
    val isProcessingNoteCreation = _isProcessingNoteCreation

    fun createNote(note: Note) = viewModelScope.launch {
        _isProcessingNoteCreation.value = true
        invokeCreateNoteUseCase(note)
    }

    private suspend fun invokeCreateNoteUseCase(note: Note) =
        noteUseCases.createNoteUseCase.invoke(note)

    fun validateNoteDetails(title: String, message: String) =
        title.trim().isNotEmpty() && message.trim().isNotEmpty()
}