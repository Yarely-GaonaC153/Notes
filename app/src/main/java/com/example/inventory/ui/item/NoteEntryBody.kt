package com.example.inventory.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoteEntryBody(
    noteUiState: NoteUiState,
    onValueChange: (NoteDetails) -> Unit, // <--- AQUÍ ESTABA EL PROBLEMA, ahora recibe todo junto
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NoteInputForm(
            noteDetails = noteUiState.noteDetails,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSaveClick,
            enabled = noteUiState.isEntryValid, // Esto arregla el error de "isEntryValid"
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun NoteInputForm(
    noteDetails: NoteDetails,
    onValueChange: (NoteDetails) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = noteDetails.title,
            onValueChange = { onValueChange(noteDetails.copy(title = it)) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = noteDetails.content,
            onValueChange = { onValueChange(noteDetails.copy(content = it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = false,
            minLines = 3
        )
    }
}