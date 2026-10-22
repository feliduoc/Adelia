package com.example.grupo_5_biosur.model

/** Una foto, una nota de voz o una nota de texto (o una mezcla de ellas). */
data class Entry(
    val id: Long,
    val hora: String,
    val texto: String? = null,
    val rutaFoto: String? = null,
    val rutaAudio: String? = null
)

/** Un suceso de la obra: una entrada principal con sus adjuntos conectados. */
data class Suceso(
    val id: Long,
    val principal: Entry,
    val adjuntos: List<Entry> = emptyList()
)

// Solo se usa en las previsualizaciones de Android Studio
val sucesosDeEjemplo = listOf(
    Suceso(
        id = 1,
        principal = Entry(1, "12:00 am", texto = "Estructura de madera del primer piso", rutaFoto = "ejemplo"),
        adjuntos = listOf(
            Entry(2, "12:04 am", rutaAudio = "ejemplo"),
            Entry(3, "12:10 am", texto = "Falta revisar el arriostramiento del muro norte.")
        )
    ),
    Suceso(
        id = 4,
        principal = Entry(4, "2:00 pm", texto = "Losa del segundo nivel", rutaFoto = "ejemplo")
    )
)
