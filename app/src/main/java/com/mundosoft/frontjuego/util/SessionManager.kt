package com.mundosoft.frontjuego.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "front_juego_prefs"
    private var sharedPreferences: SharedPreferences? = null

    // Claves para guardar los datos
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"

    private const val KEY_TOTAL_POINTS = "total_points"

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private val editor: SharedPreferences.Editor?
        get() = sharedPreferences?.edit()

    fun saveAuthToken(token: String) {
        editor?.putString(KEY_AUTH_TOKEN, token)?.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences?.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUser(userId: Int, username: String) {
        editor?.apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun getUserId(): Int {
        return sharedPreferences?.getInt(KEY_USER_ID, -1) ?: -1
    }

    fun getUsername(): String? {
        return sharedPreferences?.getString(KEY_USERNAME, null)
    }


    /**
     *  iniciar sesión o en la primera carga del perfil.
     */
    fun saveTotalPoints(points: Int) {
        editor?.putInt(KEY_TOTAL_POINTS, points)?.apply()
    }

    fun getTotalPoints(): Int {
        return sharedPreferences?.getInt(KEY_TOTAL_POINTS, 0) ?: 0
    }

    fun addPoints(pointsToAdd: Int) {
        val currentPoints = getTotalPoints()
        val newTotal = currentPoints + pointsToAdd
        saveTotalPoints(newTotal)
        println("PUNTOS_LOCALES: Se añadieron $pointsToAdd puntos. Nuevo total local: $newTotal")
    }

    fun clearSession() {
        editor?.clear()?.apply()
    }
}