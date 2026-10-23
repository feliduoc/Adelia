package com.example.grupo_5_biosur.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FORMATO_CLAVE = "yyyy-MM-dd"

/** Fecha de hoy como texto "2026-10-22"; sirve para identificar y ordenar los días. */
fun claveDeHoy(): String =
    SimpleDateFormat(FORMATO_CLAVE, Locale.US).format(Date())

/** Convierte "2026-10-22" en "Jueves 22 de octubre de 2026". */
fun fechaLarga(clave: String): String {
    val fecha: Date = try {
        SimpleDateFormat(FORMATO_CLAVE, Locale.US).parse(clave)
    } catch (e: Exception) {
        null
    } ?: return clave
    val espanolChile = Locale.Builder().setLanguage("es").setRegion("CL").build()
    return SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", espanolChile)
        .format(fecha)
        .replaceFirstChar { it.uppercase() }
}
