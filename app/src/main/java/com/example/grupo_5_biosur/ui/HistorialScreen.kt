package com.example.grupo_5_biosur.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grupo_5_biosur.model.Carpeta
import com.example.grupo_5_biosur.model.claveDeHoy
import com.example.grupo_5_biosur.model.fechaLarga
import com.example.grupo_5_biosur.model.sucesosDeEjemplo
import com.example.grupo_5_biosur.ui.theme.Grupo_5_biosurTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class ModoVista { LISTA, CALENDARIO }

/** Devuelve la etiqueta relativa de la fecha ("Hoy", "Ayer", "Hace 5 días", "Hace 1 semana", etc.). */
fun etiquetaFechaRelativa(fechaClave: String): String {
    val hoyClave = claveDeHoy()
    if (fechaClave == hoyClave) return "Hoy"

    return try {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val fechaHoy = formato.parse(hoyClave) ?: return fechaClave
        val fechaItem = formato.parse(fechaClave) ?: return fechaClave

        val diferenciaMs = fechaHoy.time - fechaItem.time
        val dias = (diferenciaMs / (1000 * 60 * 60 * 24)).toInt()

        when {
            dias <= 0 -> "Hoy"
            dias == 1 -> "Ayer"
            dias in 2..6 -> "Hace $dias días"
            dias in 7..13 -> "Hace 1 semana"
            dias in 14..29 -> "Hace ${dias / 7} semanas"
            dias in 30..59 -> "Hace 1 mes"
            else -> "Hace ${dias / 30} meses"
        }
    } catch (_: Exception) {
        fechaClave
    }
}

/** Lista de los días en que se registraron sucesos, con opción de vista en Lista o Calendario. */
@Composable
fun HistorialScreen(
    carpetas: List<Carpeta>,
    idHoy: Long,
    onAbrir: (Long) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var modoVista by remember { mutableStateOf(ModoVista.LISTA) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        "Historial de días",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    // Alternador entre Vista Lista y Calendario
                    IconButton(
                        onClick = {
                            modoVista = if (modoVista == ModoVista.LISTA) ModoVista.CALENDARIO else ModoVista.LISTA
                        }
                    ) {
                        Icon(
                            imageVector = if (modoVista == ModoVista.LISTA) Icons.Default.CalendarMonth else Icons.AutoMirrored.Filled.List,
                            contentDescription = "Cambiar vista"
                        )
                    }
                }
            }

            if (modoVista == ModoVista.CALENDARIO) {
                item {
                    CalendarioDias(
                        carpetas = carpetas,
                        onAbrir = onAbrir
                    )
                }
            } else {
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
                            Text(
                                text = etiquetaFechaRelativa(carpeta.fecha),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
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
}

@Composable
private fun CalendarioDias(
    carpetas: List<Carpeta>,
    onAbrir: (Long) -> Unit
) {
    val hoyClave = claveDeHoy() // "yyyy-MM-dd"
    val partes = hoyClave.split("-")
    val anio = partes.getOrNull(0)?.toIntOrNull() ?: 2026
    val mes = partes.getOrNull(1)?.toIntOrNull() ?: 10

    val calendario = Calendar.getInstance().apply {
        set(Calendar.YEAR, anio)
        set(Calendar.MONTH, mes - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val diasEnMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH)
    val primerDiaSemana = (calendario.get(Calendar.DAY_OF_WEEK) + 5) % 7 // 0 = Lunes, ..., 6 = Domingo
    val nombreMes = SimpleDateFormat("MMMM yyyy", Locale.Builder().setLanguage("es").setRegion("CL").build())
        .format(calendario.time)
        .replaceFirstChar { it.uppercase() }

    val carpetasPorFecha = remember(carpetas) {
        carpetas.associateBy { it.fecha }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = nombreMes,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Encabezados de días
        val diasSemana = listOf("L", "M", "M", "J", "V", "S", "D")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val totalCeldas = primerDiaSemana + diasEnMes
        val filas = (totalCeldas + 6) / 7

        for (f in 0 until filas) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (c in 0 until 7) {
                    val indiceCelda = f * 7 + c
                    val numeroDia = indiceCelda - primerDiaSemana + 1

                    if (numeroDia in 1..diasEnMes) {
                        val fechaClave = "%04d-%02d-%02d".format(anio, mes, numeroDia)
                        val carpeta = carpetasPorFecha[fechaClave]
                        val esHoy = fechaClave == hoyClave
                        val tieneSucesos = (carpeta?.sucesos?.size ?: 0) > 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        esHoy -> MaterialTheme.colorScheme.primary
                                        tieneSucesos -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = carpeta != null) {
                                    carpeta?.let { onAbrir(it.id) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$numeroDia",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (esHoy) Color.White else Color.Unspecified
                                    )
                                )
                                if (tieneSucesos && !esHoy) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
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
                Carpeta(1, "2026-10-22", "Obra Casa Pérez", sucesosDeEjemplo),
                Carpeta(2, "2026-10-21", "Obra Casa Pérez", sucesosDeEjemplo.take(1))
            ),
            idHoy = 1,
            onAbrir = {},
            onVolver = {}
        )
    }
}