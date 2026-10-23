package com.example.grupo_5_biosur.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grupo_5_biosur.model.Carpeta
import com.example.grupo_5_biosur.model.fechaLarga
import com.example.grupo_5_biosur.model.sucesosDeEjemplo
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme

/** Lista de los días en que se registraron sucesos, del más reciente al más antiguo. */
@Composable
fun HistorialScreen(
    carpetas: List<Carpeta>,
    idHoy: Long,
    onAbrir: (Long) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Se muestran los días con registros y siempre el de hoy
    val visibles = carpetas
        .filter { it.sucesos.isNotEmpty() || it.id == idHoy }
        .sortedByDescending { it.fecha }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Text("Historial de días", style = MaterialTheme.typography.titleLarge)
                }
            }
            items(visibles, key = { it.id }) { carpeta ->
                val cantidad = carpeta.sucesos.size
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onAbrir(carpeta.id) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = carpeta.nombre,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            modifier = Modifier.weight(1f)
                        )
                        if (carpeta.id == idHoy) {
                            Text(
                                "Hoy",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(fechaLarga(carpeta.fecha), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "$cantidad ${if (cantidad == 1) "suceso" else "sucesos"}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialScreenPreview() {
    Grupo_5_biosurTheme {
        HistorialScreen(
            carpetas = listOf(
                Carpeta(1, "2026-10-22", "Obra casa Pérez", sucesosDeEjemplo),
                Carpeta(2, "2026-10-21", "Obra casa Pérez", sucesosDeEjemplo.take(1))
            ),
            idHoy = 1,
            onAbrir = {},
            onVolver = {}
        )
    }
}
