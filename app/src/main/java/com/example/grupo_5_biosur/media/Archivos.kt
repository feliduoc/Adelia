package com.example.grupo_5_biosur.media

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun carpetaFotos(contexto: Context): File =
    File(contexto.filesDir, "fotos").apply { mkdirs() }

fun carpetaAudios(contexto: Context): File =
    File(contexto.filesDir, "audios").apply { mkdirs() }

fun horaActual(): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
