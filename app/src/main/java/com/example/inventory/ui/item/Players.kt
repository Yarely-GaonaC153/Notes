package com.example.inventory.ui.item

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh // Puedes cambiar esto por un icono de Pausa si tienes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(
    videoUri: Uri?,
    modifier: Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            videoUri?.let { MediaItem.fromUri(it) }?.let { setMediaItem(it) }
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
    )
}

@Composable
fun AudioPlayer(
    audioUri: Uri?
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

    DisposableEffect(audioUri) {
        if (audioUri != null) {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(context, audioUri)
                mediaPlayer.prepare()
                isPrepared = true

                mediaPlayer.setOnCompletionListener {
                    isPlaying = false
                }
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Error loading audio", e)
            }
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                if (isPrepared) {
                    if (isPlaying) {
                        mediaPlayer.pause()
                    } else {
                        mediaPlayer.start()
                    }
                    isPlaying = !isPlaying
                }
            }
        ) {
            // Nota: Usa Icons.Filled.Pause si lo tienes disponible, aquí usé Refresh como en tu original
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Refresh else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = if (isPlaying) "Reproduciendo..." else "Listo para reproducir")
    }
}