package org.example.project.domain

import kotlinx.coroutines.flow.Flow
import org.example.project.database.NoteEntity

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun insertNote(title: String, content: String)
    suspend fun deleteNote(id: Long)
    suspend fun getNoteById(id: Long): NoteEntity?
    suspend fun updateNote(id: Long, title: String, content: String)
}