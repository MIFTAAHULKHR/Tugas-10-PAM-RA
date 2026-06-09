package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NEWEST
)

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val settingsDataSource: SettingsDataSource
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState: StateFlow<NotesUiState> = combine(
        noteRepository.getAllNotes(),
        _searchQuery,
        settingsDataSource.sortOrder
    ) { notes, query, sortOrder ->
        val filteredNotes = if (query.isBlank()) {
            notes
        } else {
            notes.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) 
            }
        }

        val sortedNotes = when (sortOrder) {
            SortOrder.NEWEST -> filteredNotes.sortedByDescending { it.createdAt }
            SortOrder.OLDEST -> filteredNotes.sortedBy { it.createdAt }
            SortOrder.TITLE -> filteredNotes.sortedBy { it.title }
        }

        NotesUiState(
            isLoading = false,
            notes = sortedNotes,
            searchQuery = query,
            sortOrder = sortOrder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch {
            settingsDataSource.setSortOrder(order)
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            noteRepository.insertNote(note)
        }
    }

    fun updateNote(id: Long, title: String, content: String) {
        viewModelScope.launch {
            noteRepository.updateNote(id, title, content)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
        }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteRepository.toggleFavorite(note.id!!, !note.isFavorite)
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return noteRepository.getNoteById(id)
    }
}
