package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun App() {
    // Navigasi sederhana menggunakan state
    var currentScreen by remember { mutableStateOf("main") }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "main",
                        onClick = { currentScreen = "main" },
                        label = { Text("Notes") },
                        icon = { Icon(Icons.Default.List, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "settings",
                        onClick = { currentScreen = "settings" },
                        label = { Text("Settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (currentScreen == "main") {
                    MainScreen()
                } else {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 1. Inject Dependensi melalui Koin
    val networkMonitor: NetworkMonitor = koinInject()
    val noteDataSource: NoteDataSource = koinInject()

    // 2. Observasi Status Jaringan
    val isConnected by networkMonitor.isConnected.collectAsState()

    // 3. UI State untuk Catatan (Loading, Empty, Content)
    // Mengambil data dari SQLDelight Flow yang sudah kita buat sebelumnya
    val notes by noteDataSource.getAllNotes().collectAsState(initial = emptyList())
    var isLoading by remember { mutableStateOf(true) }

    // Simulasi loading sebentar
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- INDIKATOR JARINGAN ---
        Surface(
            color = if (isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (isConnected) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Aplikasi Online" else "Aplikasi Offline (Lokal)",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }

        // --- LOGIKA UI STATE (Loading, Empty, Content) ---
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            notes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada catatan. Silakan tambah baru!", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(notes) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    // 4. Inject DeviceInfo melalui Koin (expect/actual)
    val deviceInfo: DeviceInfo = koinInject()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Card Informasi Perangkat
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Informasi Perangkat", fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Model: ${deviceInfo.model}", style = MaterialTheme.typography.bodyLarge)
                Text("OS: ${deviceInfo.osVersion}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}