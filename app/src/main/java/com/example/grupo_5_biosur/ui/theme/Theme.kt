package com.example.grupo_5_biosur.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulFab,
    secondary = CelesteAudio,
    tertiary = RojoCaptura,
    background = Fondo,
    surface = Fondo,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal
)

@Composable
fun Grupo_5_biosurTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
