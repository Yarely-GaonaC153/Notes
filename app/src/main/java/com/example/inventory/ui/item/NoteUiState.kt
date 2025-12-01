package com.example.inventory.ui.item

import com.example.inventory.data.Note

/**
 * Estado de la UI para la pantalla de entrada/edición.
 */
data class NoteUiState(
    val noteDetails: NoteDetails = NoteDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Datos que captura la UI.
 */
data class NoteDetails(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val fecha: Long = 0L,
    val hora: Long = 0L,
    val multimediaUris: List<String> = emptyList() // Campo necesario para los audios
)

/**
 * Función de extensión para convertir NoteDetails (UI) a Note (Base de datos)
 */
fun NoteDetails.toNote(): Note = Note(
    id = id,
    title = title,
    content = content,
    fecha = fecha,
    hora = hora,
    multimediaUris = multimediaUris
)

/**
 * Función de extensión para convertir Note (Base de datos) a NoteDetails (UI)
 */
fun Note.toNoteDetails(): NoteDetails = NoteDetails(
    id = id,
    title = title,
    content = content,
    fecha = fecha,
    hora = hora,
    multimediaUris = multimediaUris
)