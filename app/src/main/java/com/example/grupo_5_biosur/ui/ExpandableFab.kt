package com.example.grupo_5_biosur.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private enum class OpcionSeleccionada { NINGUNA, MIC, CAMARA, TECLADO }

/**
 * Botón flotante ultra fluido (60fps+) que reacciona a los 50ms.
 * Al soltar el dedo sin haber arrastrado hacia una opción, revierte automáticamente su estado.
 */
@Composable
fun ExpandableFab(
    onMicrofono: () -> Unit,
    onCamara: () -> Unit,
    onTeclado: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    var opcionDestacada by remember { mutableStateOf(OpcionSeleccionada.NINGUNA) }
    val scopeCorrutina = rememberCoroutineScope()

    Box(modifier = modifier.size(width = 220.dp, height = 180.dp)) {
        OpcionFab(
            visible = expandido || opcionDestacada != OpcionSeleccionada.NINGUNA,
            destacada = opcionDestacada == OpcionSeleccionada.MIC,
            icono = Icons.Default.Mic,
            descripcion = "Nota de voz",
            desplazamientoX = (-76).dp,
            desplazamientoY = (-56).dp
        ) {
            expandido = false
            onMicrofono()
        }
        OpcionFab(
            visible = expandido || opcionDestacada != OpcionSeleccionada.NINGUNA,
            destacada = opcionDestacada == OpcionSeleccionada.CAMARA,
            icono = Icons.Default.CameraAlt,
            descripcion = "Cámara",
            desplazamientoX = 0.dp,
            desplazamientoY = (-88).dp
        ) {
            expandido = false
            onCamara()
        }
        OpcionFab(
            visible = expandido || opcionDestacada != OpcionSeleccionada.NINGUNA,
            destacada = opcionDestacada == OpcionSeleccionada.TECLADO,
            icono = Icons.Default.Keyboard,
            descripcion = "Nota de texto",
            desplazamientoX = 76.dp,
            desplazamientoY = (-56).dp
        ) {
            expandido = false
            onTeclado()
        }

        val contextoPx = LocalDensity.current
        val umbralPx = with(contextoPx) { 24.dp.toPx() }

        FloatingActionButton(
            onClick = { },
            shape = CircleShape,
            containerColor = if (opcionDestacada != OpcionSeleccionada.NINGUNA)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(72.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var acumuladoX = 0f
                        var acumuladoY = 0f
                        var seleccionActual = OpcionSeleccionada.NINGUNA

                        val temporizadorRetencion = scopeCorrutina.launch {
                            delay(50)
                            expandido = true
                        }

                        while (true) {
                            val event = awaitPointerEvent()
                            val cambio = event.changes.firstOrNull() ?: break

                            if (cambio.pressed) {
                                val delta = cambio.positionChange()
                                acumuladoX += delta.x
                                acumuladoY += delta.y

                                val distanciaCuadrada = acumuladoX * acumuladoX + acumuladoY * acumuladoY
                                if (distanciaCuadrada > umbralPx * umbralPx) {
                                    if (!expandido) expandido = true
                                    seleccionActual = when {
                                        abs(acumuladoY) > abs(acumuladoX) && acumuladoY < 0 -> OpcionSeleccionada.CAMARA
                                        abs(acumuladoX) > abs(acumuladoY) && acumuladoX < 0 -> OpcionSeleccionada.MIC
                                        abs(acumuladoX) > abs(acumuladoY) && acumuladoX > 0 -> OpcionSeleccionada.TECLADO
                                        else -> OpcionSeleccionada.NINGUNA
                                    }
                                    opcionDestacada = seleccionActual
                                }
                                cambio.consume()
                            } else {
                                temporizadorRetencion.cancel()
                                if (seleccionActual != OpcionSeleccionada.NINGUNA) {
                                    when (seleccionActual) {
                                        OpcionSeleccionada.CAMARA -> onCamara()
                                        OpcionSeleccionada.MIC -> onMicrofono()
                                        OpcionSeleccionada.TECLADO -> onTeclado()
                                        else -> {}
                                    }
                                    expandido = false
                                } else {
                                    // Al soltar sin seleccionar una opción, revierte/cierra el estado
                                    expandido = false
                                }
                                opcionDestacada = OpcionSeleccionada.NINGUNA
                                break
                            }
                        }
                    }
                }
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Agregar entrada")
        }
    }
}

@Composable
private fun BoxScope.OpcionFab(
    visible: Boolean,
    destacada: Boolean = false,
    icono: ImageVector,
    descripcion: String,
    desplazamientoX: Dp,
    desplazamientoY: Dp,
    onClick: () -> Unit
) {
    val escala by animateFloatAsState(
        targetValue = if (destacada) 1.25f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "EscalaFab"
    )
    val colorContenedor = if (destacada) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(x = desplazamientoX, y = desplazamientoY)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            },
        enter = scaleIn(animationSpec = tween(150, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(150)),
        exit = scaleOut(animationSpec = tween(150, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(150))
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = colorContenedor,
            contentColor = Color.White,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icono, contentDescription = descripcion)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableFabPreview() {
    Grupo_5_biosurTheme {
        ExpandableFab(onMicrofono = {}, onCamara = {}, onTeclado = {})
    }
}