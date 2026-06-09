# Notes App — Tugas 10: Implementasi DI & Testing

Aplikasi manajemen catatan berbasis **Kotlin Multiplatform (Compose Multiplatform)** yang telah dikembangkan dengan penambahan **Dependency Injection (Koin)** dan suite pengujian lengkap meliputi unit test, flow test, dan UI test.

---

## 📋 Deskripsi Tugas

Implementasi DI dan Testing untuk Notes App:

1. **Setup Koin DI** dengan minimal 2 modules (`data`, `viewModel`)
2. **Unit test untuk NoteRepository** (minimal 5 test cases)
3. **Unit test untuk NotesViewModel dengan MockK** (minimal 4 test cases)
4. **Flow test dengan Turbine** (minimal 2 test cases)
5. **UI test untuk NotesScreen** (minimal 3 test cases)
6. **Minimum code coverage 60%** untuk business logic

---

## 🏗️ Arsitektur & Dependency Injection

Proyek menggunakan arsitektur berlapis dengan Koin sebagai DI framework, diorganisasi dalam dua modul utama.

### Struktur Modul Koin

**`dataModule`** — mengelola layer data:
```kotlin
val dataModule = module {
    single<NoteRepository> {
        NoteRepositoryImpl(get())
    }
}
```

**`viewModelModule`** — mengelola ViewModel:
```kotlin
val viewModelModule = module {
    factory {
        NoteViewModel(get(), get())
    }
}
```

**`platformModule`** — modul platform-spesifik (Android):
```kotlin
actual fun platformModule(): Module = module {
    single { NetworkMonitor(get()) }
    single {
        val driver = DatabaseDriverFactory(get()).createDriver()
        NoteDatabase(driver)
    }
    single { DataStoreFactory(get()).create() }
}
```

### Inisialisasi Koin
```kotlin
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(dataModule, viewModelModule, platformModule())
}
```

---

## 🧪 Implementasi Testing

### 1. Unit Test — NoteRepository (5 Test Cases)

File: `commonTest/.../data/NoteRepositoryImplTest.kt`

Menggunakan **JDBC SQLite in-memory driver** agar dapat berjalan di JVM tanpa emulator Android.

```kotlin
@BeforeTest
fun setup() {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    NoteDatabase.Schema.create(driver)
    database = NoteDatabase(driver)
    repository = NoteRepositoryImpl(database)
}
```

| # | Test Case | Deskripsi |
|---|-----------|-----------|
| 1 | `insert note should increase count` | Memverifikasi jumlah catatan bertambah setelah insert |
| 2 | `get note by id should return correct note` | Memverifikasi catatan yang diambil sesuai dengan yang diinsert |
| 3 | `get note by id non existent should return null` | Memverifikasi return `null` untuk ID yang tidak ada |
| 4 | `update note should change content` | Memverifikasi perubahan judul dan konten setelah update |
| 5 | `delete note should remove from list` | Memverifikasi catatan terhapus dari daftar |

### 2. Unit Test — NotesViewModel dengan MockK (4 Test Cases)

File: `androidUnitTest/.../NoteViewModelTest.kt`

Menggunakan **MockK** untuk mem-mock dependensi `NoteRepository` dan `SettingsDataSource`.

```kotlin
@MockK
private lateinit var repository: NoteRepository

@MockK
private lateinit var settingsDataSource: SettingsDataSource

@BeforeTest
fun setup() {
    MockKAnnotations.init(this)
    viewModel = NoteViewModel(repository, settingsDataSource)
}
```

| # | Test Case | Deskripsi |
|---|-----------|-----------|
| 1 | `initial uiState should emit loading` | Memverifikasi state awal menampilkan loading |
| 2 | `addNote should call insertNote on repository` | Memverifikasi `insertNote` dipanggil dengan parameter yang benar |
| 3 | `deleteNote should call deleteNote on repository` | Memverifikasi `deleteNote` dipanggil dengan ID yang benar |
| 4 | `updateNote should call updateNote on repository` | Memverifikasi `updateNote` dipanggil dengan semua parameter yang benar |

### 3. Flow Test dengan Turbine (2 Test Cases)

File: `commonTest/.../NoteViewModelFlowTest.kt`

Menggunakan **Turbine** untuk menguji emisi `StateFlow` dan `Flow` secara berurutan.

```kotlin
@Test
fun `uiState should emit notes after loading`() = runTest {
    val testNotes = listOf(Note(id = 1L, title = "Test", content = "Content"))
    every { repository.getAllNotes() } returns flowOf(testNotes)

    viewModel.uiState.test {
        val initial = awaitItem()
        assertTrue(initial.isLoading)
        val loaded = awaitItem()
        assertEquals(1, loaded.notes.size)
        cancelAndIgnoreRemainingEvents()
    }
}
```

| # | Test Case | Deskripsi |
|---|-----------|-----------|
| 1 | `uiState should emit notes after loading` | Memverifikasi urutan emisi dari loading ke daftar catatan |
| 2 | `searchQuery update should filter notes in uiState` | Memverifikasi flow filter catatan bereaksi terhadap perubahan query |

### 4. UI Test — NotesScreen (3 Test Cases)

File: `androidTest/.../NotesScreenTest.kt`

Menggunakan **Compose UI Testing** dengan `createComposeRule` dan MockK untuk mengisolasi UI dari logika bisnis.

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun `notes screen should display empty state message when list is empty`() {
    composeTestRule.setContent {
        NotesScreen(viewModel = viewModel)
    }
    composeTestRule.onNodeWithText("Belum ada catatan").assertIsDisplayed()
}
```

| # | Test Case | Deskripsi |
|---|-----------|-----------|
| 1 | `notes screen should display empty state message` | Memverifikasi pesan empty state tampil saat tidak ada catatan |
| 2 | `notes list should display all notes from uiState` | Memverifikasi semua catatan dari state ditampilkan di layar |
| 3 | `clicking delete button should call deleteNote on viewModel` | Memverifikasi interaksi tombol hapus memicu fungsi ViewModel yang sesuai |

---

## 📦 Dependensi Testing

```kotlin
// commonTest
implementation("app.cash.turbine:turbine:1.1.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
implementation("io.insert-koin:koin-test:3.5.3")
implementation(libs.kotlin.test)

// androidUnitTest
implementation("io.mockk:mockk:1.13.8")
implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
implementation("androidx.test:core:1.5.0")

// androidTest (UI Test)
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.3")
androidTestImplementation("io.mockk:mockk-android:1.13.8")
debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.3")
```

---

## 📁 Struktur Proyek

```
composeApp/src/
├── commonMain/kotlin/org/example/project/
│   ├── di/
│   │   └── KoinCommon.kt          # dataModule & viewModelModule
│   ├── domain/
│   │   └── NoteRepository.kt      # Interface Repository
│   ├── data/
│   │   └── NoteRepositoryImpl.kt  # Implementasi Repository (SQLDelight)
│   ├── NoteViewModel.kt           # ViewModel dengan StateFlow
│   ├── Note.kt                    # Data model
│   └── SettingsDataSource.kt      # DataStore preferences
│
├── androidMain/kotlin/.../
│   └── di/
│       └── KoinAndroid.kt         # platformModule (Android)
│
├── commonTest/kotlin/.../
│   └── data/
│       └── NoteRepositoryImplTest.kt  # 5 unit test repository
│
└── androidUnitTest/kotlin/.../
    └── data/
        └── TestUtils.kt           # In-memory driver helper
```

---

## 🚀 Cara Menjalankan Test

```bash
# Jalankan semua unit test (commonTest + androidUnitTest)
./gradlew :composeApp:testDebugUnitTest

# Jalankan UI test (membutuhkan emulator/device)
./gradlew :composeApp:connectedAndroidTest

# Generate laporan code coverage
./gradlew :composeApp:koverHtmlReport
```

---

## 🚀 Cara Menjalankan Aplikasi

1. **Clone repositori:**
   ```bash
   git clone https://github.com/MIFTAAHULKHR/Tugas-10-PAM-RA.git
   ```
2. **Buka proyek** menggunakan Android Studio (versi Ladybug atau lebih baru).
3. **Build & Run** pada target perangkat Android atau iOS.

---

**Disusun oleh:**  
**Miftahul Khoiriyah** (123140064)  

