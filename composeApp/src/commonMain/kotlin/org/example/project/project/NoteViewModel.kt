package org.example.project.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.database.NoteEntity
import org.example.project.NoteRepository
import org.example.project.SettingsDataSource

class NoteViewModel(
    private val repository: NoteRepository,
    private val settingsDataSource: SettingsDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<NoteEntity>>(emptyList())
    val uiState: StateFlow<List<NoteEntity>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllNotes()
                .onEach { /* mapping logic if needed */ }
                .onCompletion { _isLoading.value = false }
                .collect()
        }
    }
}
