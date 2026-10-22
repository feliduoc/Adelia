package com.example.grupo_5_biosur.model

import androidx.compose.runtime.mutableStateListOf

/**
 * Guarda los sucesos en memoria mientras la app está abierta.
 * En el paso 7 se reemplaza por almacenamiento permanente.
 */
object Repositorio {
    val sucesos = mutableStateListOf<Suceso>()

    /** Crea un suceso nuevo cuya entrada principal es la indicada. */
    fun nuevoSuceso(principal: Entry) {
        sucesos.add(Suceso(id = principal.id, principal = principal))
    }

    /** Conecta una entrada a un suceso que ya existe. */
    fun adjuntar(sucesoId: Long, entrada: Entry) {
        val posicion = sucesos.indexOfFirst { it.id == sucesoId }
        if (posicion >= 0) {
            val actual = sucesos[posicion]
            sucesos[posicion] = actual.copy(adjuntos = actual.adjuntos + entrada)
        }
    }
}
