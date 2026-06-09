package org.example.project.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import androidx.test.core.app.ApplicationProvider
import org.example.project.database.NoteDatabase

actual fun createInMemorySqlDriver(): SqlDriver {
    return AndroidSqliteDriver(
        NoteDatabase.Schema,
        ApplicationProvider.getApplicationContext(),
        null // null untuk in-memory
    )
}