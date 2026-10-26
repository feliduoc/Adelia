package com.example.grupo_5_biosur.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grupo_5_biosur.model.Entry
import com.example.grupo_5_biosur.model.Suceso
import com.example.grupo_5_biosur.model.sucesosDeEjemplo
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun FolderScreen(
    nombre: String,
    fecha: String,
    sucesos: List<Suceso>,
    onAnexarCamara: (Long) -> Unit,
    onAnexarMicrofono: (Long) -> Unit,
    onAnexarTeclado: (Long) -> Unit,
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
                    IconButton(onClick = onHistorial) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú / Historial de días")
                    }
                    Spacer(Modifier.width(8.dp))
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
                    onAnexarCamara = { onAnexarCamara(suceso.id) },
                    onAnexarMicrofono = { onAnexarMicrofono(suceso.id) },
                    onAnexarTeclado = { onAnexarTeclado(suceso.id) }
                )
            }
        }
    }
}

private enum class OpcionCentrada { NINGUNA, CAMARA, MIC, TECLADO }

/**
 * Un suceso: mantiene su posición de diseño 100% inamovible (sin centrarse ni shift)
 * y despliega las opciones centradas exactamente en las coordenadas de toque con la misma animación que el FAB.
 */
@Composable
fun SucesoCard(
    suceso: Suceso,
    derecha: Boolean,
    onAnexarCamara: () -> Unit,
    onAnexarMicrofono: () -> Unit,
    onAnexarTeclado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorLinea = MaterialTheme.colorScheme.primary
    var menuActivo by remember { mutableStateOf(false) }
    var origenTouch by remember { mutableStateOf(Offset.Zero) }
    var opcionDestacada by remember { mutableStateOf(OpcionCentrada.NINGUNA) }
    val scopeCorrutina = rememberCoroutineScope()
    val contextoPx = LocalDensity.current
    val umbralPx = with(contextoPx) { 24.dp.toPx() }

    Column(modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = if (derecha) Alignment.TopEnd else Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            origenTouch = down.position
                            var acumuladoX = 0f
                            var acumuladoY = 0f
                            var seleccionActual = OpcionCentrada.NINGUNA

                            val temporizadorRetencion = scopeCorrutina.launch {
                                delay(50)
                                menuActivo = true
                            }

                            while (true) {
                                val evento = awaitPointerEvent()
                                val cambio = evento.changes.firstOrNull() ?: break

                                if (cambio.pressed) {
                                    val delta = cambio.positionChange()
                                    acumuladoX += delta.x
                                    acumuladoY += delta.y

                                    val distanciaCuadrada = acumuladoX * acumuladoX + acumuladoY * acumuladoY
                                    if (distanciaCuadrada > umbralPx * umbralPx) {
                                        if (!menuActivo) menuActivo = true
                                        seleccionActual = when {
                                            abs(acumuladoY) > abs(acumuladoX) && acumuladoY < 0 -> OpcionCentrada.CAMARA
                                            abs(acumuladoX) > abs(acumuladoY) && acumuladoX < 0 -> OpcionCentrada.MIC
                                            abs(acumuladoX) > abs(acumuladoY) && acumuladoX > 0 -> OpcionCentrada.TECLADO
                                            else -> OpcionCentrada.NINGUNA
                                        }
                                        opcionDestacada = seleccionActual
                                    }
                                    cambio.consume()
                                } else {
                                    temporizadorRetencion.cancel()
                                    if (seleccionActual != OpcionCentrada.NINGUNA) {
                                        when (seleccionActual) {
                                            OpcionCentrada.CAMARA -> onAnexarCamara()
                                            OpcionCentrada.MIC -> onAnexarMicrofono()
                                            OpcionCentrada.TECLADO -> onAnexarTeclado()
                                            else -> {}
                                        }
                                    }
                                    menuActivo = false
                                    opcionDestacada = OpcionCentrada.NINGUNA
                                    break
                                }
                            }
                        }
                    }
            ) {
                EntryCard(entrada = suceso.principal)

                // Superposición animada idéntica al FAB, sin mover la tarjeta y centrada exactamente en el toque
                androidx.compose.animation.AnimatedVisibility(
                    visible = menuActivo || opcionDestacada != OpcionCentrada.NINGUNA,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150)),
                    modifier = Modifier.matchParentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = with(LocalDensity.current) { origenTouch.x.toDp() - 110.dp },
                                    y = with(LocalDensity.current) { origenTouch.y.toDp() - 110.dp + 78.dp }
                                )
                                .size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BotonOpcionCentrado(
                                destacado = opcionDestacada == OpcionCentrada.MIC,
                                icono = Icons.Default.Mic,
                                descripcion = "Nota de voz",
                                desplazamientoX = (-76).dp,
                                desplazamientoY = (-56).dp,
                                onClick = onAnexarMicrofono
                            )
                            BotonOpcionCentrado(
                                destacado = opcionDestacada == OpcionCentrada.CAMARA,
                                icono = Icons.Default.CameraAlt,
                                descripcion = "Cámara",
                                desplazamientoX = 0.dp,
                                desplazamientoY = (-88).dp,
                                onClick = onAnexarCamara
                            )
                            BotonOpcionCentrado(
                                destacado = opcionDestacada == OpcionCentrada.TECLADO,
                                icono = Icons.Default.Keyboard,
                                descripcion = "Nota de texto",
                                desplazamientoX = 76.dp,
                                desplazamientoY = (-56).dp,
                                onClick = onAnexarTeclado
                            )

                            FloatingActionButton(
                                onClick = { menuActivo = false },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    }
                }
            }
        }

        if (suceso.adjuntos.isNotEmpty()) {
            // Estilo simplificado con espacio superior para despegar la línea del borde del elemento
            val modificadorCadena = if (derecha) {
                Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp)
                    .drawBehind {
                        val espacioSuperior = 6.dp.toPx()
                        drawLine(
                            color = colorLinea,
                            start = Offset(size.width, espacioSuperior),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    .padding(end = 12.dp)
            } else {
                Modifier
                    .padding(start = 12.dp)
                    .drawBehind {
                        val espacioSuperior = 6.dp.toPx()
                        drawLine(
                            color = colorLinea,
                            start = Offset(0f, espacioSuperior),
                            end = Offset(0f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    .padding(start = 12.dp)
            }

            Column(
                modifier = modificadorCadena,
                horizontalAlignment = if (derecha) Alignment.End else Alignment.Start
            ) {
                suceso.adjuntos.forEach { adjunto ->
                    EntryCard(
                        entrada = adjunto,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.BotonOpcionCentrado(
    destacado: Boolean,
    icono: ImageVector,
    descripcion: String,
    desplazamientoX: Dp = 0.dp,
    desplazamientoY: Dp = 0.dp,
    onClick: () -> Unit
) {
    val escala by animateFloatAsState(
        targetValue = if (destacado) 1.25f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "EscalaCentrado"
    )
    val color = if (destacado) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    androidx.compose.animation.AnimatedVisibility(
        visible = true,
        enter = scaleIn(animationSpec = tween(150, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(150)),
        exit = scaleOut(animationSpec = tween(150, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(150))
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = color,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = desplazamientoX, y = desplazamientoY)
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
                .size(44.dp)
        ) {
            Icon(icono, contentDescription = descripcion)
        }
    }
}

@Composable
fun EntryCard(entrada: Entry, modifier: Modifier = Modifier) {
    val sinContenido =
        entrada.rutaFoto == null && entrada.texto == null && entrada.rutaAudio == null

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(entrada.hora, style = MaterialTheme.typography.labelSmall)
            entrada.ubicacionFormateada?.let { gps ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Ubicación GPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = gps,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
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

/** Reemplaza al botón flotante mientras se graba una nota de voz, con animación de ondas. */
@Composable
fun BarraGrabacion(
    segundos: Int,
    onDetener: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transicionInfinita = rememberInfiniteTransition(label = "OndasAudioTransition")
    val h1 by transicionInfinita.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transicionInfinita.animateFloat(
        initialValue = 1f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transicionInfinita.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "h3"
    )

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

        Row(
            modifier = Modifier.height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(Modifier.width(4.dp).height((20 * h1).dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
            Box(Modifier.width(4.dp).height((20 * h2).dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
            Box(Modifier.width(4.dp).height((20 * h3).dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
            Box(Modifier.width(4.dp).height((20 * h1).dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
        }

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
            nombre = "Obra BioSur - Fase 1",
            fecha = "Jueves 22 de octubre de 2026",
            sucesos = sucesosDeEjemplo,
            onAnexarCamara = {},
            onAnexarMicrofono = {},
            onAnexarTeclado = {},
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