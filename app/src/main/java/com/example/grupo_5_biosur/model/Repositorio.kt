package com.example.grupo_5_biosur.model

import androidx.compose.runtime.mutableStateListOf

private const val NOMBRE_INICIAL = "Nombre de carpeta"

/**
 * Guarda las carpetas (una por día) en memoria mientras la app está abierta.
 * Más adelante se reemplaza por almacenamiento permanente.
 */
object Repositorio {
    val carpetas = mutableStateListOf<Carpeta>()

    /**
     * Devuelve el id de la carpeta de hoy y la crea si todavía no existe.
     * La carpeta nueva parte con el nombre de la carpeta más reciente, para no
     * tener que escribir el nombre de la obra todos los días.
     */
    fun asegurarCarpetaDeHoy(): Long {
        val hoy = claveDeHoy()
        carpetas.firstOrNull { it.fecha == hoy }?.let { return it.id }
        val nombre = carpetas.maxByOrNull { it.fecha }?.nombre ?: NOMBRE_INICIAL
        val nueva = Carpeta(id = System.currentTimeMillis(), fecha = hoy, nombre = nombre)
        carpetas.add(nueva)
        return nueva.id
    }

    private fun actualizar(carpetaId: Long, cambio: (Carpeta) -> Carpeta) {
        val posicion = carpetas.indexOfFirst { it.id == carpetaId }
        if (posicion >= 0) carpetas[posicion] = cambio(carpetas[posicion])
    }

    /** Crea un suceso nuevo en la carpeta, con la entrada indicada como principal. */
    fun nuevoSuceso(carpetaId: Long, principal: Entry) {
        actualizar(carpetaId) {
            it.copy(sucesos = it.sucesos + Suceso(id = principal.id, principal = principal))
        }
    }

    /** Conecta una entrada a un suceso que ya existe. */
    fun adjuntar(carpetaId: Long, sucesoId: Long, entrada: Entry) {
        actualizar(carpetaId) { carpeta ->
            carpeta.copy(
                sucesos = carpeta.sucesos.map {
                    if (it.id == sucesoId) it.copy(adjuntos = it.adjuntos + entrada) else it
                }
            )
        }
    }

    fun renombrar(carpetaId: Long, nombre: String) {
        actualizar(carpetaId) { it.copy(nombre = nombre) }
    }
}
