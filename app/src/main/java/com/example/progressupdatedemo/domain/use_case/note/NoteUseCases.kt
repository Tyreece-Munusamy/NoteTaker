package com.example.progressupdatedemo.domain.use_case.note

data class NoteUseCases(
    val createNoteUseCase: CreateNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val getNotesUseCase: GetNotesUseCase,
    val getNoteUseCase: GetNoteUseCase
)
