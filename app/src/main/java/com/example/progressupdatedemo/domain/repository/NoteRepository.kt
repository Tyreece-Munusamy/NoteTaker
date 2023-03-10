package com.example.progressupdatedemo.domain.repository

import com.example.progressupdatedemo.data.Response
import com.example.progressupdatedemo.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun createNote(note: Note): Response<Any>

    fun getNotes(): Flow<Response<List<Note>>>

    fun getNote(noteUUID: String): Flow<Response<Note>>

    suspend fun deleteNote(note: Note): Response<Any>

    suspend fun updateNote(updatedNote: Note): Response<Any>
}