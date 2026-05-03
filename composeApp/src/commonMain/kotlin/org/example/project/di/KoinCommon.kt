package org.example.project.di

import org.example.project.NoteDataSource
import org.example.project.DeviceInfo
import org.example.project.SettingsDataSource
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { NoteDataSource(get()) }
    single { DeviceInfo() }
    single { SettingsDataSource(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule, platformModule())
    }

expect fun platformModule(): Module
