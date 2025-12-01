package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.NotesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
) : ViewModel() {

    var noteUiState by mutableStateOf(NoteUiState())
        private set

    private val noteId: Int = checkNotNull(savedStateHandle["noteId"])

    init {
        viewModelScope.launch {
            // Obtenemos la nota de la base de datos
            val note = notesRepository.getNoteStream(noteId)
                .filterNotNull()
                .first()

            // Asignamos el estado.
            // Usamos toNoteDetails() directamente porque el flujo ya filtró los nulos
            noteUiState = NoteUiState(noteDetails = note.toNoteDetails(), isEntryValid = true)
        }
    }

    fun updateTitle(newTitle: String) {
        noteUiState = noteUiState.copy(
            // QUITAMOS EL ? PORQUE noteDetails YA NO ES NULO
            noteDetails = noteUiState.noteDetails.copy(title = newTitle)
        )
    }

    fun updateContent(newContent: String) {
        noteUiState = noteUiState.copy(
            // QUITAMOS EL ? AQUÍ TAMBIÉN
            noteDetails = noteUiState.noteDetails.copy(content = newContent)
        )
    }

    // Función para guardar los cambios
    fun updateNote() {
        if (validateInput(noteUiState.noteDetails)) {
            viewModelScope.launch {
                notesRepository.updateNote(noteUiState.noteDetails.toNote())
            }
        }
    }

    private fun validateInput(uiState: NoteDetails = noteUiState.noteDetails): Boolean {
        return uiState.title.isNotBlank() && uiState.content.isNotBlank()
    }
}