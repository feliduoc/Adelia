package com.example.grupo_5_biosur.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class GrabadorAudio(private val contexto: Context) {

    private var grabador: MediaRecorder? = null
    private var archivo: File? = null

    /** Empieza a grabar. Devuelve false si no se pudo iniciar. */
    fun iniciar(): Boolean {
        val destino = File(carpetaAudios(contexto), "audio_${System.currentTimeMillis()}.m4a")
        val nuevo = crearGrabador(contexto)
        return try {
            nuevo.setAudioSource(MediaRecorder.AudioSource.MIC)
            nuevo.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            nuevo.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            nuevo.setOutputFile(destino.absolutePath)
            nuevo.prepare()
            nuevo.start()
            grabador = nuevo
            archivo = destino
            true
        } catch (e: Exception) {
            nuevo.release()
            destino.delete()
            false
        }
    }

    /** Detiene la grabación y devuelve la ruta del audio, o null si falló (por ejemplo, muy corta). */
    fun detener(): String? {
        val actual = grabador ?: return null
        val destino = archivo
        grabador = null
        archivo = null
        return try {
            actual.stop()
            destino?.absolutePath
        } catch (e: RuntimeException) {
            destino?.delete()
            null
        } finally {
            actual.release()
        }
    }

    /** Detiene y descarta la grabación en curso. */
    fun cancelar() {
        detener()?.let { File(it).delete() }
    }
}

@Suppress("DEPRECATION")
private fun crearGrabador(contexto: Context): MediaRecorder =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(contexto) else MediaRecorder()
