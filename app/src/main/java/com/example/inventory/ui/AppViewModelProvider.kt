package com.example.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.InventoryApplication
import com.example.inventory.ui.item.NoteDetailsViewModel
import com.example.inventory.ui.item.NoteEditViewModel
import com.example.inventory.ui.item.NoteEntryViewModel
import com.example.inventory.ui.notes.NotesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Inicializar NoteEditViewModel
        initializer {
            NoteEditViewModel(
                // AGREGA ESTA LÍNEA (El orden depende de tu constructor, aquí lo pongo primero):
                savedStateHandle = this.createSavedStateHandle(),
                notesRepository = inventoryApplication().container.notesRepository
            )
        }

        // Inicializar NoteEntryViewModel
        initializer {
            NoteEntryViewModel(inventoryApplication().container.notesRepository)
        }

        // Inicializar NoteDetailsViewModel
        initializer {
            NoteDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                notesRepository = inventoryApplication().container.notesRepository
            )
        }

        // Inicializar NotesViewModel (Pantalla principal)
        initializer {
            NotesViewModel(inventoryApplication().container.notesRepository)
        }
    }
}

/**
 * Función de extensión para consultar el objeto [Application] y devolver una instancia de
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)