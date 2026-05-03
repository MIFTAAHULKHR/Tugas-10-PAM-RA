package org.example.project

import kotlinx.coroutines.flow.StateFlow

expect class NetworkMonitor {
    val isConnected: StateFlow<Boolean>
}