package com.example.grupo_5_biosur.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme

/**
 * Botón flotante que al tocarlo despliega tres opciones:
 * micrófono (izquierda), cámara (arriba) y teclado (derecha).
 */
@Composable
fun ExpandableFab(
    onMicrofono: () -> Unit,
    onCamara: () -> Unit,
    onTeclado: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by rememberSaveable { mutableStateOf(false) }

    // El contenedor es más grande que el botón para que las opciones
    // queden dentro de sus límites y reciban los toques.
    Box(modifier = modifier.size(width = 220.dp, height = 180.dp)) {
        OpcionFab(
            visible = expandido,
            icono = Icons.Default.Mic,
            descripcion = "Nota de voz",
            desplazamientoX = (-76).dp,
            desplazamientoY = (-56).dp
        ) {
            expandido = false
            onMicrofono()
        }
        OpcionFab(
            visible = expandido,
            icono = Icons.Default.CameraAlt,
            descripcion = "Cámara",
            desplazamientoX = 0.dp,
            desplazamientoY = (-88).dp
        ) {
            expandido = false
            onCamara()
        }
        OpcionFab(
            visible = expandido,
            icono = Icons.Default.Keyboard,
            descripcion = "Nota de texto",
            desplazamientoX = 76.dp,
            desplazamientoY = (-56).dp
        ) {
            expandido = false
            onTeclado()
        }

        FloatingActionButton(
            onClick = { expandido = !expandido },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(72.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Agregar entrada")
        }
    }
}

@Composable
private fun BoxScope.OpcionFab(
    visible: Boolean,
    icono: ImageVector,
    descripcion: String,
    desplazamientoX: Dp,
    desplazamientoY: Dp,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(x = desplazamientoX, y = desplazamientoY),
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
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
