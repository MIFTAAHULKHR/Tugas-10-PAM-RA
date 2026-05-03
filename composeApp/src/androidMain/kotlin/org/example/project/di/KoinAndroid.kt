package org.example.project.di

import org.example.project.DataStoreFactory
import org.example.project.DatabaseDriverFactory
import org.example.project.NetworkMonitor
import org.example.project.database.NoteDatabase
import org.koin.dsl.module
import org.koin.core.module.Module

actual fun platformModule(): Module = module {
    // 1. Menyediakan NetworkMonitor
    single { NetworkMonitor(get()) }

    // 2. Menyediakan NoteDatabase
    // Adapter dihapus karena SQLDelight menangani Boolean secara native
    single {
        val driver = DatabaseDriverFactory(get()).createDriver()
        NoteDatabase(driver)
    }

    // 3. Menyediakan DataStore untuk SettingsDataSource
    single { DataStoreFactory(get()).create() }
}