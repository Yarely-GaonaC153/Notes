package com.example.inventory.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.NoteApplication
import com.example.inventory.ui.item.NoteEntryViewModel
import com.example.inventory.ui.item.NoteEditViewModel
import com.example.inventory.ui.notes.NoteDetailsViewModel
import com.example.inventory.ui.notes.NotesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            NoteEntryViewModel(
                notesRepository = inventoryApplication().container.notesRepository
            )
        }

        // Inicializar NoteEditViewModel
        initializer {
            NoteEditViewModel(
                notesRepository = inventoryApplication().container.notesRepository
            )
        }
        initializer {
            NotesViewModel(
                notesRepository = inventoryApplication().container.notesRepository
            )
        }
        // Inicializar NoteDetailsViewModel con SavedStateHandle
        initializer {
            NoteDetailsViewModel(
                notesRepository = inventoryApplication().container.notesRepository,
                savedStateHandle = createSavedStateHandle() // Llamada sin contexto explícito
            )
        }
    }
}

fun CreationExtras.inventoryApplication(): NoteApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NoteApplication)
