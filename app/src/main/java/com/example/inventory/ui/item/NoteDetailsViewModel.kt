package com.example.inventory.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.NotesRepository
import com.example.inventory.ui.item.NoteDetailsDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Definimos el estado de la UI
data class NoteDetailsUiState(
    val noteDetails: NoteDetails = NoteDetails()
)

class NoteDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailsDestination.itemIdArg])

    // Aquí está la variable 'uiState' que tu pantalla busca
    val uiState: StateFlow<NoteDetailsUiState> =
        notesRepository.getNoteStream(noteId)
            .filterNotNull()
            .map {
                NoteDetailsUiState(noteDetails = it.toNoteDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = NoteDetailsUiState()
            )

    // Aquí está la función 'deleteItem' que tu pantalla busca
    fun deleteItem() {
        viewModelScope.launch {
            val currentNote = uiState.value.noteDetails.toNote()
            notesRepository.deleteNote(currentNote)
        }
    }
}