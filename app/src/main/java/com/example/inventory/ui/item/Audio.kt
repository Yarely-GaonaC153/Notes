package com.example.inventory.ui.item

import android.Manifest
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.IOException

@Composable
fun AudioRecorderButton(
    existingAudioPaths: List<String>,
    onAudioRecorded: (String) -> Unit,
    onDeleteAudio: (String) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var currentFile: File? by remember { mutableStateOf(null) }

    // --- FILTRO MÁGICO ---
    // Solo mostramos en esta lista lo que sea AUDIO.
    // Las fotos y videos se quedan ocultos para no borrarlos por error.
    val audioOnlyList = existingAudioPaths.filter { path ->
        path.endsWith(".mp3") || path.endsWith(".3gp") || path.endsWith(".m4a")
    }

    val startRecording = {
        val fileName = "audio_${System.currentTimeMillis()}.mp3"
        val file = File(context.cacheDir, fileName)
        currentFile = file

        val newRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
                Log.d("AudioRecorder", "Grabando en: ${file.absolutePath}")
            } catch (e: IOException) {
                Log.e("AudioRecorder", "Falló al iniciar: ${e.message}")
            }
        }
        recorder = newRecorder
    }

    val stopRecording = {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false

            currentFile?.let {
                if (it.exists()) {
                    onAudioRecorded(it.absolutePath)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error al detener: ${e.message}")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted && !isRecording) {
            startRecording()
        }
    }

    Column {
        // Botón de grabar
        IconButton(onClick = {
            if (hasPermission) {
                if (isRecording) stopRecording() else startRecording()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Icon(
                imageVector = if (isRecording) Icons.Filled.Done else Icons.Filled.PlayArrow,
                contentDescription = "Grabar",
                tint = if (isRecording) Color.Red else Color.Black
            )
        }

        if (isRecording) {
            Text("Grabando...", color = Color.Red, modifier = Modifier.padding(start = 8.dp))
        }

        // --- AQUI USAMOS LA LISTA FILTRADA ---
        audioOnlyList.forEachIndexed { index, path ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {

                Text(
                    text = "Audio ${index + 1}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

                val file = File(path)
                if(file.exists()){
                    // AudioPlayer(audioUri = android.net.Uri.fromFile(file))
                }

                IconButton(onClick = { onDeleteAudio(path) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = Color.Red)
                }
            }
            // Reproductor
            val file = File(path)
            if(file.exists()){
                AudioPlayer(audioUri = android.net.Uri.fromFile(file))
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder?.release()
        }
    }
}