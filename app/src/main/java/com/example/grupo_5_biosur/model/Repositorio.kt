package com.example.grupo_5_biosur.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

object Repositorio {
    var obraAsignada by mutableStateOf(ObraAsignada(101L, "Obra BioSur - Fase 1", "Av. San Martín 450, Concepción", listOf("Juan Pérez (Capataz)", "Nicolás Silva (Inspector)")))
    var trabajadorActual by mutableStateOf(Trabajador(1L, "Nicolás Silva", "Inspector de Terreno"))
    val carpetas = mutableStateListOf<Carpeta>()

    init { inicializarDatosEjemplo() }

    private fun inicializarDatosEjemplo() {
        val hoy = claveDeHoy()
        val partes = hoy.split("-")
        val a = partes.getOrNull(0) ?: "2026"
        val m = partes.getOrNull(1) ?: "10"
        carpetas.addAll(listOf(
            Carpeta(1001L, "$a-$m-05", obraAsignada.nombre, listOf(Suceso(501L, Entry(501, "09:15 am", texto = "Trazado y excavación de cimientos", latitud = -36.8275, longitud = -73.0508)))),
            Carpeta(1002L, "$a-$m-12", obraAsignada.nombre, listOf(Suceso(502L, Entry(502, "11:30 am", texto = "Hormigonado de enfierradura de pilares", latitud = -36.8280, longitud = -73.0510), listOf(Entry(503, "11:45 am", texto = "Se verificó la densidad del hormigón previo al fraguado."))))),
            Carpeta(1003L, "$a-$m-18", obraAsignada.nombre, listOf(Suceso(504L, Entry(504, "02:20 pm", texto = "Instalación de moldajes en losa primer nivel", latitud = -36.8270, longitud = -73.0501))))
        ))
    }

    fun asegurarCarpetaDeHoy(): Long {
        val hoy = claveDeHoy()
        carpetas.firstOrNull { it.fecha == hoy }?.let { return it.id }
        val nombre = obraAsignada.nombre
        val nueva = Carpeta(System.currentTimeMillis(), hoy, nombre)
        carpetas.add(nueva)
        return nueva.id
    }

    private fun actualizar(id: Long, cambio: (Carpeta) -> Carpeta) {
        val pos = carpetas.indexOfFirst { it.id == id }
        if (pos >= 0) carpetas[pos] = cambio(carpetas[pos])
    }

    fun nuevoSuceso(carpetaId: Long, principal: Entry) = actualizar(carpetaId) { it.copy(sucesos = it.sucesos + Suceso(principal.id, principal)) }
    fun adjuntar(carpetaId: Long, sucesoId: Long, entrada: Entry) = actualizar(carpetaId) { c ->
        c.copy(sucesos = c.sucesos.map { if (it.id == sucesoId) it.copy(adjuntos = it.adjuntos + entrada) else it })
    }
    fun renombrar(carpetaId: Long, nombre: String) = actualizar(carpetaId) { it.copy(nombre = nombre) }
    fun obtenerGpsSimulado(): Pair<Double, Double> = Pair(-36.8270 + (Random.nextDouble() - 0.5) * 0.005, -73.0503 + (Random.nextDouble() - 0.5) * 0.005)
}