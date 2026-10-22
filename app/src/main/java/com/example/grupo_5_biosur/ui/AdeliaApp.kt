package com.example.grupo_5_biosur.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.grupo_5_biosur.media.GrabadorAudio
import com.example.grupo_5_biosur.media.horaActual
import com.example.grupo_5_biosur.model.Entry
import com.example.grupo_5_biosur.model.Repositorio
import kotlinx.coroutines.delay

private fun avisar(contexto: Context, mensaje: String) {
    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
}

private fun tienePermiso(contexto: Context, permiso: String): Boolean =
    ContextCompat.checkSelfPermission(contexto, permiso) == PackageManager.PERMISSION_GRANTED

@Composable
fun AdeliaApp() {
    val contexto = LocalContext.current
    var camaraAbierta by rememberSaveable { mutableStateOf(false) }
    var grabando by remember { mutableStateOf(false) }
    var segundos by remember { mutableIntStateOf(0) }
    val grabador = remember { GrabadorAudio(contexto) }

    // Suceso al que se está adjuntando algo (null = se crea un suceso nuevo)
    var destino by remember { mutableStateOf<Long?>(null) }
    // Suceso tocado, mientras se muestra el menú de adjuntar
    var sucesoElegido by remember { mutableStateOf<Long?>(null) }
    var mostrarNota by remember { mutableStateOf(false) }
    var textoNota by rememberSaveable { mutableStateOf("") }

    // Si la app se cierra a mitad de una grabación, la descarta
    DisposableEffect(Unit) {
        onDispose { grabador.cancelar() }
    }

    // Cuenta los segundos mientras se graba
    LaunchedEffect(grabando) {
        segundos = 0
        while (grabando) {
            delay(1000)
            segundos++
        }
    }

    /** Guarda la entrada: como adjunto del suceso elegido o como suceso nuevo. */
    fun agregar(entrada: Entry) {
        val id = destino
        if (id == null) Repositorio.nuevoSuceso(entrada) else Repositorio.adjuntar(id, entrada)
        destino = null
    }

    fun empezarGrabacion() {
        if (grabador.iniciar()) {
            grabando = true
        } else {
            destino = null
            avisar(contexto, "No se pudo iniciar la grabación")
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            camaraAbierta = true
        } else {
            destino = null
            avisar(contexto, "Se necesita el permiso de cámara para tomar fotos")
        }
    }

    val permisoAudio = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            empezarGrabacion()
        } else {
            destino = null
            avisar(contexto, "Se necesita el permiso de micrófono para grabar")
        }
    }

    fun abrirCamara() {
        if (tienePermiso(contexto, Manifest.permission.CAMERA)) camaraAbierta = true
        else permisoCamara.launch(Manifest.permission.CAMERA)
    }

    fun abrirMicrofono() {
        if (tienePermiso(contexto, Manifest.permission.RECORD_AUDIO)) empezarGrabacion()
        else permisoAudio.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun cerrarNota() {
        mostrarNota = false
        textoNota = ""
        destino = null
    }

    if (camaraAbierta) {
        CamaraScreen(
            onFotoTomada = { ruta ->
                agregar(Entry(id = System.currentTimeMillis(), hora = horaActual(), rutaFoto = ruta))
                camaraAbierta = false
            },
            onCerrar = {
                camaraAbierta = false
                destino = null
            }
        )
    } else {
        FolderScreen(
            nombre = "Nombre de carpeta",
            sucesos = Repositorio.sucesos,
            onSucesoClick = { id -> sucesoElegido = id },
            grabando = grabando,
            segundosGrabando = segundos,
            onMicrofono = {
                destino = null
                abrirMicrofono()
            },
            onCamara = {
                destino = null
                abrirCamara()
            },
            onTeclado = {
                destino = null
                mostrarNota = true
            },
            onDetenerGrabacion = {
                val ruta = grabador.detener()
                grabando = false
                if (ruta != null) {
                    agregar(Entry(id = System.currentTimeMillis(), hora = horaActual(), rutaAudio = ruta))
                } else {
                    destino = null
                    avisar(contexto, "La grabación fue muy corta")
                }
            },
            onCancelarGrabacion = {
                grabador.cancelar()
                grabando = false
                destino = null
            }
        )
    }

    // Menú para adjuntar algo a un suceso
    sucesoElegido?.let { id ->
        AlertDialog(
            onDismissRequest = { sucesoElegido = null },
            title = { Text("Adjuntar a este suceso") },
            text = {
                Column {
                    Text(
                        "Lo que agregues quedará conectado a esta entrada.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    OpcionAdjuntar(Icons.Default.CameraAlt, "Agregar foto") {
                        destino = id
                        sucesoElegido = null
                        abrirCamara()
                    }
                    OpcionAdjuntar(Icons.Default.Mic, "Agregar nota de voz") {
                        destino = id
                        sucesoElegido = null
                        abrirMicrofono()
                    }
                    OpcionAdjuntar(Icons.Default.Keyboard, "Agregar nota de texto") {
                        destino = id
                        sucesoElegido = null
                        mostrarNota = true
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { sucesoElegido = null }) { Text("Cancelar") }
            }
        )
    }

    // Cuadro para escribir una nota de texto
    if (mostrarNota) {
        AlertDialog(
            onDismissRequest = { cerrarNota() },
            title = {
                Text(if (destino == null) "Nueva nota de texto" else "Nota para este suceso")
            },
            text = {
                OutlinedTextField(
                    value = textoNota,
                    onValueChange = { textoNota = it },
                    placeholder = { Text("Escribe lo que ocurrió…") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val texto = textoNota.trim()
                    if (texto.isNotEmpty()) {
                        agregar(Entry(id = System.currentTimeMillis(), hora = horaActual(), texto = texto))
                    }
                    cerrarNota()
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { cerrarNota() }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun OpcionAdjuntar(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}
