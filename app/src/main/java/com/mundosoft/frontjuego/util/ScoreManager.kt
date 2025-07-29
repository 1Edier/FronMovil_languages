package com.mundosoft.frontjuego.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Un gestor dedicado para guardar los puntajes de los usuarios de forma persistente.
 * Usa un archivo de SharedPreferences separado para que los puntajes no se borren al cerrar sesión.
 */
object ScoreManager {
    private const val PREFS_NAME = "user_scores_prefs"
    private var sharedPreferences: SharedPreferences? = null

    // Usaremos un prefijo para guardar el score de cada usuario. Ej: "score_123"
    private const val KEY_USER_SCORE_PREFIX = "score_"

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private val editor: SharedPreferences.Editor?
        get() = sharedPreferences?.edit()

    /**
     * Guarda un valor absoluto de puntos para un usuario específico.
     */
    fun saveScoreForUser(userId: Int, score: Int) {
        if (userId == -1) return
        editor?.putInt("$KEY_USER_SCORE_PREFIX$userId", score)?.apply()
    }

    /**
     * Obtiene el puntaje guardado para un usuario específico.
     */
    fun getScoreForUser(userId: Int): Int {
        if (userId == -1) return 0
        return sharedPreferences?.getInt("$KEY_USER_SCORE_PREFIX$userId", 0) ?: 0
    }

    /**
     * Suma puntos al total de un usuario específico.
     */
    fun addPointsToUser(userId: Int, pointsToAdd: Int) {
        if (userId == -1) return
        val currentScore = getScoreForUser(userId)
        val newTotal = currentScore + pointsToAdd
        saveScoreForUser(userId, newTotal)
        println("PUNTOS_PERSISTENTES: Se añadieron $pointsToAdd puntos al usuario $userId. Nuevo total: $newTotal")
    }
}