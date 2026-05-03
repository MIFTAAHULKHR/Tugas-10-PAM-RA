package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.example.project.di.initKoin        // Import fungsi initKoin
import org.koin.android.ext.koin.androidContext // Import androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- LETAKKAN DI SINI ---
        // Inisialisasi Koin saat aplikasi Android pertama kali dijalankan
        initKoin {
            androidContext(this@MainActivity) // Memberikan context Android ke Koin
        }
        // -------------------------

        setContent {
            // Karena sudah menggunakan DI (Koin),
            // Anda tidak perlu lagi membuat NoteDataSource secara manual di sini.
            // Cukup panggil App() dan biarkan Koin yang menyuntikkan (inject) dependensinya.
            App()
        }
    }
}