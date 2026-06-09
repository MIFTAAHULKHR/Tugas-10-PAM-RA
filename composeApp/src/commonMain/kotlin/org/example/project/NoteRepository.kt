package org.example.project

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: Long)
    suspend fun updateNote(id: Long, title: String, content: String)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}

class NoteRepositoryImpl(private val noteDataSource: NoteDataSource) : NoteRepository {
    override fun getAllNotes(): Flow<List<Note>> = noteDataSource.getAllNotes()
    override fun searchNotes(query: String): Flow<List<Note>> = noteDataSource.searchNotes(query)
    override suspend fun getNoteById(id: Long): Note? = noteDataSource.getNoteById(id)
    override suspend fun insertNote(note: Note) = noteDataSource.insertNote(note)
    override suspend fun deleteNote(id: Long) = noteDataSource.deleteNote(id)
    override suspend fun updateNote(id: Long, title: String, content: String) = noteDataSource.updateNote(id, title, content)
    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = noteDataSource.toggleFavorite(id, isFavorite)
}
