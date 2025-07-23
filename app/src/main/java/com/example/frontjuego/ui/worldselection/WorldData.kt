package com.example.frontjuego.ui.worldselection

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

// Modelo de datos para un Mundo/Nivel que usa la UI
data class World(
    val id: Int,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val difficulty: Int,
    @DrawableRes val imageRes: Int,
    val backgroundColor: Color,
    val accentColor: Color,
    val isLocked: Boolean = false,
    val completedLevels: Int = 0,
    val totalLevels: Int = 10,
    val xpReward: Int = 0,
    val unlockRequirement: String = "",
    val specialFeatures: List<String> = emptyList(),
    val gameTypes: List<String> = emptyList()
)