package org.example.project.di

import org.example.project.NoteViewModel
import org.example.project.data.NoteRepositoryImpl
import org.example.project.domain.NoteRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val dataModule = module {
    single<NoteRepository> {
        NoteRepositoryImpl(get())
    }
}

val viewModelModule = module {

    // Sesuaikan constructor NoteViewModel
    factory {
        NoteViewModel(get(), get())
    }

    // HAPUS sementara ProfileViewModel
    // factory { ProfileViewModel(get()) }
}

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()

    modules(
        dataModule,
        viewModelModule,
        platformModule()
    )
}

expect fun platformModule(): Module