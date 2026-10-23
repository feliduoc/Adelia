package com.example.grupo_5_biosur.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.grupo_5_biosur.model.Entry
import com.example.grupo_5_biosur.model.Suceso
import com.example.grupo_5_biosur.model.sucesosDeEjemplo
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FolderScreen(
    nombre: String,
    fecha: String,
    sucesos: List<Suceso>,
    onSucesoClick: (Long) -> Unit,
    onRenombrar: () -> Unit,
    onHistorial: () -> Unit,
    grabando: Boolean,
    segundosGrabando: Int,
    onMicrofono: () -> Unit,
    onCamara: () -> Unit,
    onTeclado: () -> Unit,
    onDetenerGrabacion: () -> Unit,
    onCancelarGrabacion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoLista = rememberLazyListState()

    // Cada vez que se crea un suceso nuevo, baja hasta él
    LaunchedEffect(sucesos.size) {
        if (sucesos.isNotEmpty()) estadoLista.animateScrollToItem(sucesos.size)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (grabando) {
                BarraGrabacion(
                    segundos = segundosGrabando,
                    onDetener = onDetenerGrabacion,
                    onCancelar = onCancelarGrabacion
                )
            } else {
                ExpandableFab(
                    onMicrofono = onMicrofono,
                    onCamara = onCamara,
                    onTeclado = onTeclado
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        LazyColumn(
            state = estadoLista,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onRenombrar)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = nombre,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar nombre de la carpeta",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(18.dp)
                            )
                        }
                        Text(fecha, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = onHistorial) {
                        Icon(Icons.Default.History, contentDescription = "Historial de días")
                    }
                }
            }
            if (sucesos.isEmpty()) {
                item {
                    Text(
                        "Aún no hay sucesos. Toca el botón de abajo para crear el primero con una foto, una nota de voz o una nota de texto.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            itemsIndexed(sucesos, key = { _, suceso -> suceso.id }) { index, suceso ->
                SucesoCard(
                    suceso = suceso,
                    derecha = index % 2 == 1,
                    onClick = { onSucesoClick(suceso.id) }
                )
            }
        }
    }
}

/** Un suceso: la entrada principal y, debajo, sus adjuntos unidos por una línea. */
@Composable
fun SucesoCard(
    suceso: Suceso,
    derecha: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorLinea = MaterialTheme.colorScheme.primary

    Column(modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = if (derecha) Alignment.TopEnd else Alignment.TopStart
        ) {
            EntryCard(
                entrada = suceso.principal,
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .clickable(onClick = onClick)
            )
        }

        if (suceso.adjuntos.isNotEmpty()) {
            // En un suceso de la derecha, la línea y los adjuntos quedan a la derecha
            val modificadorCadena = if (derecha) {
                Modifier
                    .fillMaxWidth()
                    .padding(end = 14.dp)
                    .drawBehind {
                        drawLine(
                            color = colorLinea,
                            start = Offset(size.width, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    .padding(end = 16.dp)
            } else {
                Modifier
                    .padding(start = 14.dp)
                    .drawBehind {
                        drawLine(
                            color = colorLinea,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    .padding(start = 16.dp)
            }

            Column(
                modifier = modificadorCadena,
                horizontalAlignment = if (derecha) Alignment.End else Alignment.Start
            ) {
                suceso.adjuntos.forEach { adjunto ->
                    EntryCard(
                        entrada = adjunto,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .drawBehind {
                                // Punto y tramo que conectan el adjunto con la línea
                                val centroX = if (derecha) size.width + 16.dp.toPx() else (-16).dp.toPx()
                                val bordeX = if (derecha) size.width else 0f
                                drawCircle(
                                    color = colorLinea,
                                    radius = 5.dp.toPx(),
                                    center = Offset(centroX, 9.dp.toPx())
                                )
                                drawLine(
                                    color = colorLinea,
                                    start = Offset(centroX, 9.dp.toPx()),
                                    end = Offset(bordeX, 9.dp.toPx()),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                            .fillMaxWidth(0.75f)
                            .clickable(onClick = onClick)
                    )
                }
            }
        }
    }
}

@Composable
fun EntryCard(entrada: Entry, modifier: Modifier = Modifier) {
    val sinContenido =
        entrada.rutaFoto == null && entrada.texto == null && entrada.rutaAudio == null

    Column(modifier) {
        Text(entrada.hora, style = MaterialTheme.typography.labelSmall)
        if (entrada.rutaFoto != null || sinContenido) {
            Spacer(Modifier.height(4.dp))
            FotoEntrada(entrada.rutaFoto)
        }
        entrada.texto?.let { texto ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .padding(4.dp)
            )
        }
        entrada.rutaAudio?.let { ruta ->
            Spacer(Modifier.height(4.dp))
            AudioBar(ruta)
        }
    }
}

/** Muestra la foto guardada; si no se puede cargar, deja un recuadro gris. */
@Composable
private fun FotoEntrada(ruta: String?, modifier: Modifier = Modifier) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta) {
        value = if (ruta == null) null else withContext(Dispatchers.IO) { cargarMiniatura(ruta) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .background(Color(0xFFB8C4CF))
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val cargada = imagen
        if (cargada != null) {
            Image(
                bitmap = cargada,
                contentDescription = "Foto de la entrada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("Foto", style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Carga la foto en tamaño reducido y corrige su rotación. */
private fun cargarMiniatura(ruta: String): ImageBitmap? {
    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
    val bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null
    val orientacion = try {
        ExifInterface(ruta).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } catch (e: Exception) {
        ExifInterface.ORIENTATION_NORMAL
    }
    val grados = when (orientacion) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    if (grados == 0f) return bitmap.asImageBitmap()
    val matriz = Matrix().apply { postRotate(grados) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        .asImageBitmap()
}

/** Reemplaza al botón flotante mientras se graba una nota de voz. */
@Composable
fun BarraGrabacion(
    segundos: Int,
    onDetener: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(Color.White)
            .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onCancelar) {
            Icon(Icons.Default.Close, contentDescription = "Cancelar grabación")
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
        )
        Text(
            text = "%02d:%02d".format(segundos / 60, segundos % 60),
            style = MaterialTheme.typography.bodyMedium
        )
        FloatingActionButton(
            onClick = onDetener,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = Color.White,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = "Detener y guardar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FolderScreenPreview() {
    Grupo_5_biosurTheme {
        FolderScreen(
            nombre = "Nombre de carpeta",
            fecha = "Jueves 22 de octubre de 2026",
            sucesos = sucesosDeEjemplo,
            onSucesoClick = {},
            onRenombrar = {},
            onHistorial = {},
            grabando = false,
            segundosGrabando = 0,
            onMicrofono = {},
            onCamara = {},
            onTeclado = {},
            onDetenerGrabacion = {},
            onCancelarGrabacion = {}
        )
    }
}
