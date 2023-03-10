package com.example.progressupdatedemo.domain.use_case.note

import com.example.progressupdatedemo.domain.repository.NoteRepository
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    operator fun invoke() = noteRepository.getNotes()
}