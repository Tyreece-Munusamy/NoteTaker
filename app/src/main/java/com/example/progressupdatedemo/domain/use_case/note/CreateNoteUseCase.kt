package com.example.progressupdatedemo.domain.use_case.note

import android.util.Log
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.repository.NoteRepository
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        val createNoteResponse = noteRepository.createNote(note)
        if (createNoteResponse is Response.Error) logCreateNoteError(createNoteResponse.message.toString())
    }

    private fun logCreateNoteError(errorMessage: String) =
        Log.d("Note Creation Error", "invoke: Failed to create note. Error: $errorMessage")
}