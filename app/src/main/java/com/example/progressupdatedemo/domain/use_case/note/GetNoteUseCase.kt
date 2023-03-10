package com.example.progressupdatedemo.domain.use_case.note

import com.example.progressupdatedemo.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    operator fun invoke(noteUUID: String) = noteRepository.getNote(noteUUID)
}