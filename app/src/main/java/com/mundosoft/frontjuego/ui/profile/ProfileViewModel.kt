package com.mundosoft.frontjuego.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundosoft.frontjuego.network.Badge
import com.mundosoft.frontjuego.network.RetrofitClient
import com.mundosoft.frontjuego.network.UserProfileData
import com.mundosoft.frontjuego.util.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (data class ProfileUiState no cambia)
data class ProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profile: UserProfileData? = null,
    val badges: List<Badge> = emptyList(),
    val username: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance

    init {
        loadProfileData()
    }


    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val userId = SessionManager.getUserId()
            val username = SessionManager.getUsername()

            if (userId == -1) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado.") }
                return@launch
            }

            _uiState.update { it.copy(username = username) }

            try {
                coroutineScope {
                    // Seguimos llamando a la API para obtener insignias y otros datos
                    val profileDeferred = async { apiService.getUserProfile(userId) }
                    val badgesDeferred = async { apiService.getUserBadges(userId) }

                    val profileResponse = profileDeferred.await()
                    val badgesResponse = badgesDeferred.await()

                    if (profileResponse.isSuccessful && badgesResponse.isSuccessful) {
                        var profileData = profileResponse.body()?.userProfile
                        val badgesData = badgesResponse.body()?.userBadges ?: emptyList()

                        if (profileData != null) {

                            val localTotalPoints = SessionManager.getTotalPoints()



                            if (localTotalPoints == 0 && profileData.totalPoints > 0) {
                                SessionManager.saveTotalPoints(profileData.totalPoints)
                            }

                            profileData = profileData.copy(totalPoints = SessionManager.getTotalPoints())

                            println("PUNTOS_LOCALES: Mostrando perfil con ${profileData.totalPoints} puntos locales.")

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    profile = profileData,
                                    badges = badgesData
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "No se pudieron cargar los datos del perfil.") }
                        }
                    } else {
                        val errorMsg = profileResponse.errorBody()?.string()
                            ?: badgesResponse.errorBody()?.string()
                            ?: "Error al cargar el perfil."
                        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }
}