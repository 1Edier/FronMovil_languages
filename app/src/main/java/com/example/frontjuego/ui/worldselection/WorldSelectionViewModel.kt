// RUTA: ui/worldselection/WorldSelectionViewModel.kt
package com.example.frontjuego.ui.worldselection
import com.example.frontjuego.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontjuego.network.RetrofitClient
import com.example.frontjuego.network.WorldInfoResponse
import com.example.frontjuego.util.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.awaitAll

// Estado de la UI para la pantalla de selección de mundos
data class WorldSelectionUiState(
    val isLoading: Boolean = true,
    val worlds: List<World> = emptyList(),
    val error: String? = null
)

class WorldSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorldSelectionUiState())
    val uiState: StateFlow<WorldSelectionUiState> = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance

    // El init se mantiene para la carga inicial.
    init {
        loadWorlds()
    }

    // --- ¡CORRECCIÓN CLAVE! ---
    // La función AHORA se llama `loadWorlds` y es pública (sin `private`).
    // Este es el cambio que soluciona el error "Unresolved reference".
    fun loadWorlds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = SessionManager.getUserId()
                if (userId == -1) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado.") }
                    return@launch
                }

                // Paso 1: Obtener la lista de mundos y el progreso del usuario en paralelo.
                val worldsResponse = apiService.getAllWorlds(1) // languageId = 1
                val progressResponse = apiService.getUserProgress(userId)

                if (worldsResponse.isSuccessful && progressResponse.isSuccessful) {
                    // Ordenamos los mundos por ID para asegurar una progresión lógica.
                    val apiWorlds = worldsResponse.body()!!.worlds.sortedBy { it.id }
                    val completedLevelsSet = progressResponse.body()!!.userProgress
                        .filter { it.status == "completed" }
                        .map { it.levelId }
                        .toSet()

                    // Paso 2: Obtener la lista de niveles para CADA mundo en paralelo.
                    val levelsByWorldId = coroutineScope {
                        apiWorlds.map { world ->
                            async {
                                val levels = apiService.getLevelsForWorld(world.id).body() ?: emptyList()
                                world.id to levels
                            }
                        }.awaitAll().toMap()
                    }

                    // Paso 3: Procesar los datos para determinar el estado de bloqueo.
                    val processedWorlds = mutableListOf<World>()
                    var canUnlockNextWorld = true // El primer mundo siempre está desbloqueado.

                    for (apiWorld in apiWorlds) {
                        val levelsForThisWorld = levelsByWorldId[apiWorld.id].orEmpty()

                        // Un mundo se considera "completo" si todos sus niveles están en la lista de completados.
                        val isThisWorldComplete = levelsForThisWorld.isNotEmpty() && levelsForThisWorld.all { level ->
                            completedLevelsSet.contains(level.id)
                        }

                        val isLocked = !canUnlockNextWorld

                        processedWorlds.add(apiWorld.toUiModel().copy(isLocked = isLocked))

                        // Se puede desbloquear el *siguiente* mundo solo si el mundo *actual* está desbloqueado Y completo.
                        canUnlockNextWorld = !isLocked && isThisWorldComplete
                    }

                    _uiState.update { it.copy(isLoading = false, worlds = processedWorlds) }

                } else {
                    val errorBody = worldsResponse.errorBody()?.string()
                        ?: progressResponse.errorBody()?.string()
                        ?: "Error desconocido al obtener los mundos"
                    _uiState.update { it.copy(isLoading = false, error = errorBody) }
                }
            } catch (e: Exception) {
                // Manejar error de conexión
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }
}

/**
 * Función de extensión para convertir el modelo de la API (WorldInfoResponse)
 * al modelo que la UI necesita (World).
 */
fun WorldInfoResponse.toUiModel(): World {
    return World(
        id = this.id,
        name = this.name,
        description = this.description,
        detailedDescription = "Una nueva aventura te espera en el mundo de '${this.name}'. ¡Prepárate para aprender!",
        difficulty = (this.id % 5) + 1,
        imageRes = when (this.id) {
            1 -> R.drawable.saludos
            2 -> R.drawable.animales
            3 -> R.drawable.casa
            4 -> R.drawable.colores
            else -> R.drawable.bosque
        },
        isLocked = true,
        backgroundColor = when (this.id) {
            1 -> Color(0xFF4CAF50)
            2 -> Color(0xFF2196F3)
            3 -> Color(0xFFFF9800)
            4 -> Color(0xFFE91E63)
            else -> Color(0xFF9C27B0)
        },
        accentColor = when (this.id) {
            1 -> Color(0xFF81C784)
            2 -> Color(0xFF64B5F6)
            3 -> Color(0xFFFFB74D)
            4 -> Color(0xFFF06292)
            else -> Color(0xFFBA68C8)
        },
        completedLevels = 0,
        totalLevels = 10,
        xpReward = 100
    )
}