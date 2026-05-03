# Note App - Kotlin Multiplatform (Upgrade: DI & Platform Features)

Aplikasi manajemen catatan modern yang dibangun menggunakan **Compose Multiplatform**. Proyek ini mengimplementasikan sistem penyimpanan data lokal yang efisien, manajemen preferensi pengguna, serta fitur lintas platform menggunakan mekanisme *expect/actual* dengan Dependency Injection terpusat menggunakan **Koin**.

Link Video Demo (45 Detik): [Tonton di YouTube](https://youtu.be/df_EwUsrPUI)

## 🏗️ Arsitektur Aplikasi
Aplikasi ini menggunakan pola arsitektur yang memisahkan logika data, bisnis, dan UI di dalam Kotlin Multiplatform untuk mendukung skalabilitas dan pengujian.

> **Catatan:** Diagram arsitektur dapat dilihat pada bagian dokumentasi di bawah sesuai dengan persyaratan format pengumpulan.

## Fitur Utama & Implementasi

### 1. Dependency Injection (Koin)
Seluruh komponen utama di-inject secara terpusat untuk menghindari instansiasi manual.
- **Injeksi Terpusat**: Mengelola `NoteDataSource`, `NetworkMonitor`, `DeviceInfo`, dan `SettingsDataSource`.
- **Platform Modules**: Pemisahan antara `appModule` (Common) dan `platformModule` (Android/iOS) untuk manajemen objek yang sesuai dengan kebutuhan spesifik masing-masing platform.

### 2. Fitur Platform (Expect/Actual)
Mengakses API native di setiap platform melalui mekanisme `expect/actual`:
- **NetworkMonitor**: Memantau koneksi secara *real-time* menggunakan `ConnectivityManager` di Android.
- **DeviceInfo**: Mendapatkan detail teknis perangkat seperti Model dan Versi OS di Android dan iOS.

### 3. Persistensi Data
- **SQLDelight**: Penyimpanan catatan secara *type-safe* di database SQLite lokal.
- **Jetpack DataStore**: Mengelola preferensi pengguna seperti tema (Light/Dark) dan urutan sortir catatan secara permanen.

---

## 🛠️ Detail Teknis & Perbaikan
Berdasarkan kendala pengembangan sebelumnya, berikut adalah konfigurasi krusial yang telah diimplementasikan:

### Izin Jaringan (Android)
Untuk menjalankan `NetworkMonitor` tanpa error `SecurityException`, izin berikut telah ditambahkan pada `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### Konfigurasi Modul Koin
Memastikan `NoteDatabase` dan `NetworkMonitor` terdaftar dengan benar di `platformModule` untuk menghindari `NoBeanDefFoundException`:
```kotlin
actual fun platformModule(): Module = module {
    // 1. NetworkMonitor
    single { NetworkMonitor(get()) }

    // 2. NoteDatabase (SQLDelight)
    single { 
        val driver = DatabaseDriverFactory(get()).createDriver()
        NoteDatabase(driver) 
    }

    // 3. DataStore
    single { DataStoreFactory(get()).create() }
}
```

---

## 📸 Dokumentasi Visual

### Status Jaringan & Informasi Perangkat
Aplikasi menampilkan indikator status jaringan pada **Main Screen** dan detail perangkat pada **Settings Screen**.

| Status Online (Main) | Status Offline (Main) |
| :---: | :---: | 
| <img src="pict/Online.png" width="250"> | <img src="pict/Offline.png" width="250"> |

---

## 🚀 Cara Menjalankan

1. **Clone & Checkout**: 
   ```bash
   git clone [https://github.com/MIFTAAHULKHR/Tugas-8-PAM-RA.git]
   git checkout week-8
   ```
   *(Pastikan menggunakan branch **week-8** sesuai format pengumpulan)*.
2. **Buka Proyek**: Gunakan Android Studio (versi Ladybug atau terbaru disarankan).
3. **Build & Run**: Pilih target perangkat (Android/iOS) dan jalankan aplikasi.

---

**Disusun Oleh:**  
**Miftahul Khoiriyah** (123140064)  
Mahasiswa Teknik Informatika Semester 6 - **Institut Teknologi Sumatera (ITERA)**

