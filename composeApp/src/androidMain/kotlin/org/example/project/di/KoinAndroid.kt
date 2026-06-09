// Lokasi: composeApp/src/androidMain/kotlin/org/example/project/di/KoinAndroid.kt
package org.example.project.di

import org.example.project.DataStoreFactory
import org.example.project.DatabaseDriverFactory
import org.example.project.NetworkMonitor
import org.example.project.database.NoteDatabase
import org.koin.dsl.module
import org.koin.core.module.Module

actual fun platformModule(): Module = module {
    single { NetworkMonitor(get()) }

    single {
        val driver = DatabaseDriverFactory(get()).createDriver()
        NoteDatabase(driver)
    }

    single { DataStoreFactory(get()).create() }
}