package org.example.project.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.example.project.database.NoteDatabase
import org.example.project.database.NoteEntity
import org.example.project.domain.NoteRepository
import kotlinx.datetime.Clock

class NoteRepositoryImpl(db: NoteDatabase) : NoteRepository {
    private val queries = db.noteDatabaseQueries

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return queries.getAllNotes().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insertNote(title: String, content: String) {
        queries.insertNote(null, title, content, false, Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun deleteNote(id: Long) {
        queries.deleteNote(id)
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return queries.getNoteById(id).executeAsOneOrNull()
    }

    override suspend fun updateNote(id: Long, title: String, content: String) {
        queries.updateNote(title, content, id)
    }
}
