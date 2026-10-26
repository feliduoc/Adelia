package com.example.grupo_5_biosur.model

data class ObraAsignada(val id: Long, val nombre: String, val direccion: String, val encargados: List<String>)
data class Trabajador(val id: Long, val nombre: String, val rol: String)

data class Entry(
    val id: Long,
    val hora: String,
    val texto: String? = null,
    val rutaFoto: String? = null,
    val rutaAudio: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
) {
    val ubicacionFormateada: String?
        get() = if (latitud != null && longitud != null) "GPS: %.4f, %.4f".format(latitud, longitud) else null
}

data class Suceso(val id: Long, val principal: Entry, val adjuntos: List<Entry> = emptyList())
data class Carpeta(val id: Long, val fecha: String, val nombre: String, val sucesos: List<Suceso> = emptyList())

val sucesosDeEjemplo = listOf(
    Suceso(1, Entry(1, "12:00 am", texto = "Estructura de madera del primer piso", rutaFoto = "ejemplo", latitud = -33.4372, longitud = -70.6506), listOf(
        Entry(2, "12:04 am", rutaAudio = "ejemplo"),
        Entry(3, "12:10 am", texto = "Falta revisar el arriostramiento del muro norte.")
    )),
    Suceso(4, Entry(4, "2:00 pm", texto = "Losa del segundo nivel", rutaFoto = "ejemplo", latitud = -33.4380, longitud = -70.6512))
)