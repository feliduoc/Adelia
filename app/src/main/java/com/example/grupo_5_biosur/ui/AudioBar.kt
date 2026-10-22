package com.example.grupo_5_biosur.ui

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Barra celeste con play/pausa y progreso para reproducir una nota de voz. */
@Composable
fun AudioBar(ruta: String, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    var reproduciendo by remember(ruta) { mutableStateOf(false) }
    var preparado by remember(ruta) { mutableStateOf(false) }
    var progreso by remember(ruta) { mutableFloatStateOf(0f) }
    val reproductor = remember(ruta) { MediaPlayer() }

    DisposableEffect(ruta) {
        onDispose { reproductor.release() }
    }

    // Actualiza la barra de progreso mientras suena el audio
    LaunchedEffect(reproduciendo) {
        while (reproduciendo) {
            val duracion = reproductor.duration
            if (duracion > 0) progreso = reproductor.currentPosition.toFloat() / duracion
            delay(100)
        }
    }

    fun alternar() {
        if (reproduciendo) {
            reproductor.pause()
            reproduciendo = false
            return
        }
        try {
            if (!preparado) {
                reproductor.setDataSource(ruta)
                reproductor.setOnCompletionListener {
                    reproduciendo = false
                    progreso = 0f
                }
                reproductor.prepare()
                preparado = true
            }
            reproductor.start()
            reproduciendo = true
        } catch (e: Exception) {
            Toast.makeText(contexto, "No se pudo reproducir el audio", Toast.LENGTH_SHORT).show()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (reproduciendo) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (reproduciendo) "Pausar" else "Reproducir",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable { alternar() }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progreso.coerceIn(0f, 1f))
                    .background(Color.White)
            )
        }
    }
}
