package com.mundosoft.frontjuego.model

import com.google.gson.annotations.SerializedName

// Modelo de datos para un Nivel del juego
data class Level(
    val id: Int,

    @SerializedName("world_id") // Coincide con el JSON "world_id"
    val worldId: Int,

    val name: String,

    @SerializedName("sort_order") // Coincide con el JSON "sort_order"
    val sortOrder: Int,

    // Puedes añadir más campos que tu API devuelva, como:
    // val isCompleted: Boolean = false,
    // val isUnlocked: Boolean = false
)