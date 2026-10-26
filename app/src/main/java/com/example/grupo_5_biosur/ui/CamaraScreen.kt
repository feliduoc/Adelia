package com.example.grupo_5_biosur.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.grupo_5_biosur.media.carpetaFotos
import java.io.File

@Composable
fun CamaraScreen(
    onFotoTomada: (String) -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val cicloVida = contexto as LifecycleOwner
    val capturaImagen = remember { ImageCapture.Builder().build() }
    val vistaPrevia = remember { PreviewView(contexto) }
    var tomandoFoto by remember { mutableStateOf(false) }

    BackHandler { onCerrar() }

    DisposableEffect(cicloVida) {
        val futuroProveedor = ProcessCameraProvider.getInstance(contexto)
        val ejecutor = ContextCompat.getMainExecutor(contexto)
        val escuchador = Runnable {
            try {
                val proveedor = futuroProveedor.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(vistaPrevia.surfaceProvider) }
                proveedor.unbindAll()
                proveedor.bindToLifecycle(cicloVida, CameraSelector.DEFAULT_BACK_CAMERA, preview, capturaImagen)
            } catch (_: Exception) {
                Toast.makeText(contexto, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
            }
        }
        futuroProveedor.addListener(escuchador, ejecutor)

        onDispose {
            try {
                if (futuroProveedor.isDone) futuroProveedor.get().unbindAll()
            } catch (_: Exception) {}
        }
    }

    fun tomarFoto() {
        if (tomandoFoto) return
        tomandoFoto = true
        val archivo = File(carpetaFotos(contexto), "foto_${System.currentTimeMillis()}.jpg")
        val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
        capturaImagen.takePicture(
            opciones,
            ContextCompat.getMainExecutor(contexto),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(res: ImageCapture.OutputFileResults) {
                    tomandoFoto = false
                    onFotoTomada(archivo.absolutePath)
                }
                override fun onError(err: ImageCaptureException) {
                    tomandoFoto = false
                    Toast.makeText(contexto, "No se pudo guardar la foto", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { vistaPrevia }, modifier = Modifier.fillMaxSize())

        IconButton(
            onClick = onCerrar,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable { tomarFoto() }
        )
    }
}
