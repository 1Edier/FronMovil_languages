package com.mundosoft.frontjuego.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundosoft.frontjuego.network.ApiErrorResponse
import com.mundosoft.frontjuego.network.LoginRequest
import com.mundosoft.frontjuego.network.RegisterRequest
import com.mundosoft.frontjuego.network.RetrofitClient
import com.mundosoft.frontjuego.util.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
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

                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {

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

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true,
                            successMessage = "¡Cuenta creada! 🎊 Ya puedes iniciar sesión."
                        )
                    }
                } else {

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