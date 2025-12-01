package com.example.inventory.ui.item

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    navigateBack: () -> Unit,
    viewModel: NoteEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Estado del ViewModel
    val noteUiState by viewModel.noteUiState.collectAsState()

    // Estados locales para los diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMultimediaPicker by remember { mutableStateOf(false) }
    var showCameraDialog by remember { mutableStateOf(false) }
    var showAudioRecorderDialog by remember { mutableStateOf(false) }

    var isReminderView by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = if (isReminderView) "Add Reminder" else "Add Note",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Switch: Notas vs Recordatorios
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isReminderView) "Recordatorios" else "Notas",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Switch(
                        checked = isReminderView,
                        onCheckedChange = { isReminderView = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }

                // Título
                OutlinedTextField(
                    value = noteUiState.noteDetails?.title.orEmpty(),
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                // Descripción
                OutlinedTextField(
                    value = noteUiState.noteDetails?.content.orEmpty(),
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text("Description") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                // Lógica de Vistas
                if (isReminderView) {
                    // --- VISTA RECORDATORIO ---
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Select Date")
                    }
                    Text(
                        text = "Selected Date: ${
                            noteUiState.noteDetails?.fecha?.takeIf { it != 0L }?.let {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                            } ?: "Not selected"
                        }",
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Button(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Select Time")
                    }
                    Text(
                        text = "Selected Time: ${
                            noteUiState.noteDetails?.hora?.takeIf { it != 0L }?.let {
                                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                            } ?: "Not selected"
                        }",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                } else {
                    // --- VISTA NOTA MULTIMEDIA ---

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { showCameraDialog = true }) {
                            Text("Camara")
                        }
                        Button(onClick = { showAudioRecorderDialog = true }) {
                            Text("Audio")
                        }
                        Button(onClick = { showMultimediaPicker = true }) {
                            Text("Galería")
                        }
                    }

                    // --- LISTA DE MULTIMEDIA (LazyColumn) ---
                    // Nota: Le ponemos altura fija para que funcione dentro del Scroll vertical principal
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(400.dp)
                    ) {
                        val currentUris = noteUiState.noteDetails?.multimediaUris ?: emptyList()

                        items(currentUris) { uriString ->
                            val uri = Uri.parse(uriString)

                            // 1. ES AUDIO
                            if (uriString.endsWith(".mp3") || uriString.endsWith(".3gp") || uriString.endsWith(".m4a")) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Nota de voz", style = MaterialTheme.typography.labelMedium)
                                            AudioPlayer(audioUri = uri)
                                        }
                                        IconButton(onClick = {
                                            viewModel.updateMultimediaUris(currentUris - uriString)
                                        }) {
                                            Icon(Icons.Filled.Delete, "Borrar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                            // 2. ES VIDEO
                            else if (uriString.endsWith(".mp4")) {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)) {
                                    // Usamos tu VideoPlayer de Players.kt
                                    VideoPlayer(
                                        videoUri = uri,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                    )
                                    // Botón borrar video
                                    IconButton(
                                        onClick = {
                                            viewModel.updateMultimediaUris(currentUris - uriString)
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(Icons.Filled.Delete, "Borrar video", tint = Color.Red)
                                    }
                                }
                            }
                            // 3. ES IMAGEN
                            else {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = uri),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.updateMultimediaUris(currentUris - uriString)
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(Icons.Filled.Delete, "Borrar imagen", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                } // <--- AQUI SE CIERRA EL ELSE (Multimedia)

                // --- BOTÓN GUARDAR (Ahora está fuera del if/else para que salga siempre) ---
                Button(
                    onClick = {
                        viewModel.saveNote(isReminderView)
                        navigateBack()
                    },
                    enabled = noteUiState.isEntryValid,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Save ${if (isReminderView) "Reminder" else "Note"}")
                }
            } // Fin de Column
        } // Fin de Scaffold content
    )

    // --- DIALOGOS ---

    if (showDatePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                viewModel.updateFecha(calendar.timeInMillis)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.updateHora(calendar.timeInMillis)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    if (showMultimediaPicker) {
        AlertDialog(
            onDismissRequest = { showMultimediaPicker = false },
            title = { Text("Select Multimedia") },
            text = {
                MultimediaPicker { selectedUris ->
                    val currentList = noteUiState.noteDetails?.multimediaUris ?: emptyList()
                    viewModel.updateMultimediaUris(currentList + selectedUris)
                }
            },
            confirmButton = {
                Button(onClick = { showMultimediaPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            title = { Text("Cámara") },
            text = {
                // Conectamos el botón de cámara (Foto/Video)
                CameraButton(
                    onMediaCaptured = { newPath ->
                        val currentList = noteUiState.noteDetails?.multimediaUris ?: emptyList()
                        viewModel.updateMultimediaUris(currentList + newPath)
                        // Opcional: showCameraDialog = false si quieres que se cierre tras tomar 1 foto
                    }
                )
            },
            confirmButton = {
                Button(onClick = { showCameraDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAudioRecorderDialog) {
        AlertDialog(
            onDismissRequest = { showAudioRecorderDialog = false },
            title = { Text("Audio Recorder") },
            text = {
                AudioRecorderButton(
                    existingAudioPaths = noteUiState.noteDetails?.multimediaUris ?: emptyList(),
                    onAudioRecorded = { newPath ->
                        val currentList = noteUiState.noteDetails?.multimediaUris ?: emptyList()
                        viewModel.updateMultimediaUris(currentList + newPath)
                    },
                    onDeleteAudio = { pathToRemove ->
                        val currentList = noteUiState.noteDetails?.multimediaUris ?: emptyList()
                        viewModel.updateMultimediaUris(currentList - pathToRemove)
                    }
                )
            },
            confirmButton = {
                Button(onClick = { showAudioRecorderDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}