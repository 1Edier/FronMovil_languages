package com.mundosoft.frontjuego.ui.levellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundosoft.frontjuego.model.Level
import com.mundosoft.frontjuego.network.RetrofitClient
import com.mundosoft.frontjuego.network.UserProgressItem
import com.mundosoft.frontjuego.util.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


enum class LevelStatus {
    LOCKED, UNLOCKED, COMPLETED
}

// Modelo de datos que usará la UI
data class UiLevel(
    val id: Int,
    val name: String,
    val sortOrder: Int,
    val status: LevelStatus
)

// Estado general de la UI para esta pantalla
data class LevelListUiState(
    val isLoading: Boolean = true,
    val levels: List<UiLevel> = emptyList(),
    val error: String? = null
)

class LevelListViewModel(private val worldId: Int) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelListUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance

    init {

        loadLevelsAndProgress()
    }

    // Hacemos la función pública para poder llamarla desde la UI cuando sea necesario.
    fun loadLevelsAndProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = SessionManager.getUserId()
            if (userId == -1) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no identificado.") }
                return@launch
            }

            try {
                // Hacemos ambas llamadas en paralelo para mayor eficiencia
                coroutineScope {
                    val levelsDeferred = async { apiService.getLevelsForWorld(worldId) }
                    val progressDeferred = async { apiService.getUserProgress(userId) }

                    val levelsResponse = levelsDeferred.await()
                    val progressResponse = progressDeferred.await()

                    if (levelsResponse.isSuccessful && progressResponse.isSuccessful) {
                        val allLevels = levelsResponse.body() ?: emptyList()
                        val userProgress = progressResponse.body()?.userProgress ?: emptyList()

                        // Procesamos los datos para crear la lista de UI
                        val uiLevels = processLevels(allLevels, userProgress)

                        _uiState.update { it.copy(isLoading = false, levels = uiLevels) }

                    } else {
                        val errorMsg = levelsResponse.errorBody()?.string() ?: progressResponse.errorBody()?.string() ?: "Error desconocido."
                        _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }

    private fun processLevels(levels: List<Level>, progress: List<UserProgressItem>): List<UiLevel> {
        val progressMap = progress.associateBy { it.levelId }
        var firstUnlockedFound = false

        return levels.sortedBy { it.sortOrder }.map { level ->
            val status = when (progressMap[level.id]?.status) {
                "completed" -> LevelStatus.COMPLETED
                else -> {
                    // Si el nivel no está completado, es el primero "desbloqueado" o está "bloqueado"
                    if (!firstUnlockedFound) {
                        firstUnlockedFound = true
                        LevelStatus.UNLOCKED
                    } else {
                        LevelStatus.LOCKED
                    }
                }
            }
            UiLevel(level.id, level.name, level.sortOrder, status)
        }
    }
}