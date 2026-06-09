package org.example.project.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver // Import baru untuk driver JVM
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.example.project.database.NoteDatabase
import org.example.project.domain.NoteRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NoteRepositoryImplTest {

    private lateinit var repository: NoteRepository
    private lateinit var database: NoteDatabase

    // Sesuai prinsip Testing, kita butuh driver database yang fresh setiap kali test dijalankan
    @BeforeTest
    fun setup() {
        // 1. Gunakan JDBC Driver in-memory murni agar bisa berjalan di JVM tanpa Android Emulator
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

        // 2. WAJIB: Buat schema tabel database di memori.
        // Tanpa ini, tes akan gagal dengan pesan "no such table"
        NoteDatabase.Schema.create(driver)

        // 3. Inject driver yang sudah siap ke dalam database dan repository
        database = NoteDatabase(driver)
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun `test 1 - insert note should increase count`() = runTest {
        repository.insertNote("Judul Kuliah", "Isi materi semester 6")

        val notes = repository.getAllNotes().first()
        assertEquals(1, notes.size)
        assertEquals("Judul Kuliah", notes[0].title)
    }

    @Test
    fun `test 2 - get note by id should return correct note`() = runTest {
        repository.insertNote("Coba ID", "Konten")
        val notes = repository.getAllNotes().first()
        val insertedId = notes[0].id

        val note = repository.getNoteById(insertedId)
        assertNotNull(note)
        assertEquals("Coba ID", note.title)
    }

    @Test
    fun `test 3 - get note by id non existent should return null`() = runTest {
        val note = repository.getNoteById(999L)
        assertNull(note)
    }

    @Test
    fun `test 4 - update note should change content`() = runTest {
        repository.insertNote("Judul Lama", "Konten Lama")
        val notes = repository.getAllNotes().first()
        val id = notes[0].id

        repository.updateNote(id, "Judul Baru", "Konten Baru")

        val updatedNote = repository.getNoteById(id)
        assertEquals("Judul Baru", updatedNote?.title)
        assertEquals("Konten Baru", updatedNote?.content)
    }

    @Test
    fun `test 5 - delete note should remove from list`() = runTest {
        repository.insertNote("Akan Dihapus", "Isi")
        val notesBefore = repository.getAllNotes().first()
        val id = notesBefore[0].id

        repository.deleteNote(id)

        val notesAfter = repository.getAllNotes().first()
        assertEquals(0, notesAfter.size)
    }
}