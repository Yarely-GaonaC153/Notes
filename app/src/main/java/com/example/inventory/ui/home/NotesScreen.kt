package com.example.inventory.ui.notes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.inventory.R
import com.example.inventory.data.Note
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import java.text.SimpleDateFormat
import java.util.*
import coil.compose.rememberAsyncImagePainter
import androidx.work.workDataOf  // Para workDataOf
import java.util.concurrent.TimeUnit  // Para TimeUnit


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
    val context = LocalContext.current
    val notesUiState by viewModel.notesUiState.collectAsState()
    var isReminderView by remember { mutableStateOf(false) } // Controla el estado del Switch

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isReminderView) "Recordatorios" else "Notas")
                },
                actions = {
                    // Switch para alternar entre Notas y Recordatorios
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Switch(
                            checked = isReminderView,
                            onCheckedChange = { isReminderView = it }
                        )
                    }

                    // Botón para agregar una nueva nota o recordatorio
                    IconButton(onClick = {
                        navigateToNoteEntry()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar ${if (isReminderView) "Recordatorio" else "Nota"}"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // Filtrar notas o recordatorios según el estado del Switch
        val filteredNotes = if (isReminderView) {
            notesUiState.notes.filter { it.fecha != 0L || it.hora != 0L } // Recordatorios
        } else {
            notesUiState.notes.filter { it.fecha == 0L && it.hora == 0L } // Notas
        }

        NotesList(
            notes = filteredNotes,
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

    // Programar notificación para la nota con fecha y hora
    if (note.fecha != 0L && note.hora != 0L) {
        scheduleReminder(note.fecha, note.hora, LocalContext.current)
    }
}

fun scheduleReminder(fecha: Long, hora: Long, context: Context) {
    val inputData = workDataOf(
        "fecha" to fecha,
        "hora" to hora
    )

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(calculateDelay(fecha, hora), TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    // Usar WorkManager para programar la tarea
    WorkManager.getInstance(context).enqueue(workRequest)
}

fun calculateDelay(fecha: Long, hora: Long): Long {
    // Crear un calendario con la fecha y hora de la notificación
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = fecha
    val horaCalendar = Calendar.getInstance()
    horaCalendar.timeInMillis = hora

    // Establecer la hora en el calendario de la notificación
    calendar.set(Calendar.HOUR_OF_DAY, horaCalendar.get(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, horaCalendar.get(Calendar.MINUTE))

    // Calcular el delay en milisegundos hasta la notificación
    val delay = calendar.timeInMillis - System.currentTimeMillis()
    return if (delay < 0) 0 else delay
}

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : androidx.work.Worker(context, workerParams) {

    override fun doWork(): Result {
        // Obtener los parámetros de la notificación (fecha y hora)
        val fecha = inputData.getLong("fecha", 0L)
        val hora = inputData.getLong("hora", 0L)

        // Programar la notificación
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "note_channel",
                "Recordatorios de Notas",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "note_channel")
            .setContentTitle("Recordatorio de Nota")
            .setContentText("Tienes un recordatorio para la nota programada.")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }
}
