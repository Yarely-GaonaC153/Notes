package com.example.inventory.ui.home

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.inventory.R
import com.example.inventory.data.Note
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotesDestination : NavigationDestination {
    override val route = "notes"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navigateToNoteEntry: () -> Unit,
    navigateToNoteDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notesUiState by viewModel.notesUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas") },
                actions = {
                    // Botón para agregar una nueva nota
                    IconButton(onClick = navigateToNoteEntry) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                }
            )
        }
    ) { padding ->
        NotesList(
            notes = notesUiState.notes,
            onNoteClick = navigateToNoteDetail,
            modifier = modifier.padding(padding)
        )
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    onNoteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(notes, key = { it.id }) { note ->
            NoteItem(note = note, onClick = { onNoteClick(note.id) })
        }
    }
}

@Composable
fun PlaySavedVideo(videoUri: Uri, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        setVideoURI(videoUri)
                        setOnPreparedListener { mediaPlayer ->
                            mediaPlayer.start() // Inicia el video automáticamente
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = note.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)

            // Mostrar Fecha solo si es válida
            if (note.fecha != 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fecha: ${dateFormatter.format(Date(note.fecha))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Mostrar Hora solo si es válida
            if (note.hora != 0L) {
                Text(
                    text = "Hora: ${timeFormatter.format(Date(note.hora))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar multimedia
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(note.multimediaUris) { uri ->
                    when {
                        uri.endsWith(".jpg") || uri.endsWith(".png") -> {
                            Image(
                                painter = rememberAsyncImagePainter(model = uri),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                        uri.endsWith(".mp3") || uri.endsWith(".wav") -> {
                            Button(
                                onClick = { /* Lógica para reproducir audio */ },
                                modifier = Modifier
                                    .padding(4.dp)
                            ) {
                                Text("Reproducir")
                            }
                        }
                        uri.endsWith(".mp4") -> {
                            Button(
                                onClick = { selectedVideoUri = Uri.parse(uri) },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text("Reproducir Video")
                            }
                        }
                        else -> {
                            Text("Formato no soportado")
                        }
                    }
                }
            }
        }
    }

    // Mostrar diálogo para reproducir video si hay un URI seleccionado
    selectedVideoUri?.let {
        PlaySavedVideo(
            videoUri = it,
            onDismiss = { selectedVideoUri = null }
        )
    }
}
