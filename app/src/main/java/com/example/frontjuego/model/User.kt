package com.example.frontjuego.model

// Modelo de datos para el usuario del perfil
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String,
    val wordsLearned: Int,
    val levelsCompleted: Int,
    val badges: List<String>
)