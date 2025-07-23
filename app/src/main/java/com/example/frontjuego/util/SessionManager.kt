package com.example.frontjuego.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "juego_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"


    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUser(userId: Int, username: String) {
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun getUserId(): Int {
        // Devuelve -1 si no se encuentra, para manejar el caso de error
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}