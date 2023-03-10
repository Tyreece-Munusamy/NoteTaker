package com.example.progressupdatedemo.domain.use_case.note

import android.util.Log
import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import com.example.progressupdatedemo.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(updatedNote: Note) {
        val updateNoteResponse = noteRepository.updateNote(updatedNote)
        if (updateNoteResponse is Response.Error) logUpdateNoteError(updateNoteResponse.message.toString())
    }

    private fun logUpdateNoteError(errorMessage: String) =
        Log.d("Note Update Error", "invoke: Failed to update note. Error: $errorMessage")

}