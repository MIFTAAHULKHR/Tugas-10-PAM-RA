package org.example.project.di

import org.example.project.NetworkMonitor
import org.koin.dsl.module
import org.koin.core.module.Module

actual fun platformModule(): Module = module {
    // iOS NetworkMonitor tidak butuh context
    single<NetworkMonitor> { NetworkMonitor() }
}
