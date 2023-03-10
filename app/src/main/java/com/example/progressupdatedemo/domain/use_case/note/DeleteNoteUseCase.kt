package com.example.progressupdatedemo.domain.use_case.note

import android.util.Log
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        val deleteNoteResponse = noteRepository.deleteNote(note)
        if (deleteNoteResponse is Response.Error) logDeleteNoteError(deleteNoteResponse.message.toString())
    }

    private fun logDeleteNoteError(errorMessage: String) =
        Log.d("Note Deletion Error", "invoke: Failed to delete note. Error: $errorMessage")
}