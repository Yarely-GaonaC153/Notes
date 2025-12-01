package com.example.inventory.ui.item

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraButton(
    onMediaCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    // Variables temporales para guardar la ruta mientras se toma la foto/video
    var tempMediaPath by remember { mutableStateOf<String?>(null) }

    // --- LAUNCHER FOTO ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempMediaPath != null) {
            onMediaCaptured(tempMediaPath!!)
        }
    }

    // --- LAUNCHER VIDEO ---
    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempMediaPath != null) {
            onMediaCaptured(tempMediaPath!!)
        }
    }

    // Solicitud de permisos (Cámara y Audio para el video)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions[Manifest.permission.CAMERA] == true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        // BOTÓN FOTO
        Button(
            onClick = {
                if (hasPermission) {
                    launchCameraPhoto(context, takePictureLauncher) { _, path ->
                        tempMediaPath = path
                    }
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Icon(Icons.Filled.AccountBox, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Tomar Foto")
        }

        // BOTÓN VIDEO
        Button(
            onClick = {
                if (hasPermission) {
                    launchCameraVideo(context, captureVideoLauncher) { _, path ->
                        tempMediaPath = path
                    }
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Grabar Video")
        }
    }
}

// --- FUNCIONES AUXILIARES ---

private fun launchCameraPhoto(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onUriCreated: (Uri, String) -> Unit
) {
    val file = createFile(context, Environment.DIRECTORY_PICTURES, ".jpg")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    onUriCreated(uri, file.absolutePath)
    launcher.launch(uri)
}

private fun launchCameraVideo(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onUriCreated: (Uri, String) -> Unit
) {
    val file = createFile(context, Environment.DIRECTORY_MOVIES, ".mp4")
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    onUriCreated(uri, file.absolutePath)
    launcher.launch(uri)
}

private fun createFile(context: Context, directory: String, suffix: String): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(directory)
    return File.createTempFile("MEDIA_${timeStamp}_", suffix, storageDir)
}