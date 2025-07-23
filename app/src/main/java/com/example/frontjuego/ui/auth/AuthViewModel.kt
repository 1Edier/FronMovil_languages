package com.example.frontjuego.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontjuego.network.ApiErrorResponse
import com.example.frontjuego.network.LoginRequest
import com.example.frontjuego.network.RegisterRequest
import com.example.frontjuego.network.RetrofitClient
import com.example.frontjuego.util.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- ¡ESTADO DE UI ACTUALIZADO! ---
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null, // Para mensajes de éxito como el registro
    val loginSuccess: Boolean = false,
    val registrationSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance
    private val gson = Gson()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginData = response.body()!!
                    SessionManager.saveAuthToken(loginData.token)
                    SessionManager.saveUser(loginData.user.id, loginData.user.username)
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                } else {
                    // --- ¡MANEJO DE ERRORES MEJORADO! ---
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            // Intenta parsear el JSON de error para obtener el mensaje
                            gson.fromJson(errorBody, ApiErrorResponse::class.java).message
                        } catch (e: Exception) {
                            // Si falla el parseo, muestra un mensaje genérico amigable
                            "Correo electrónico o contraseña incorrectos."
                        }
                    } else {
                        "Error desconocido. Por favor, inténtalo de nuevo."
                    }
                    _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo conectar al servidor. Revisa tu conexión a internet.") }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val response = apiService.register(RegisterRequest(name, email, password))
                if (response.isSuccessful) {
                    // --- ¡MENSAJE DE ÉXITO MEJORADO! ---
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true,
                            successMessage = "¡Cuenta creada! 🎊 Ya puedes iniciar sesión."
                        )
                    }
                } else {
                    // --- ¡MANEJO DE ERRORES MEJORADO! ---
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            gson.fromJson(errorBody, ApiErrorResponse::class.java).message
                        } catch (e: Exception) {
                            "No se pudo completar el registro. Inténtalo de nuevo."
                        }
                    } else {
                        "Error desconocido al registrar."
                    }
                    _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo conectar al servidor. Revisa tu conexión a internet.") }
            }
        }
    }

    fun resetStatus() {
        _uiState.update { it.copy(loginSuccess = false, registrationSuccess = false, error = null, successMessage = null) }
    }
}