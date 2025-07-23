// RUTA: ui/worldselection/WorldSelectionViewModel.kt
package com.example.frontjuego.ui.worldselection
import com.example.frontjuego.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.frontjuego.network.RetrofitClient
import com.example.frontjuego.network.WorldInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

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

    // Se llama automáticamente cuando el ViewModel es creado por primera vez
    init {
        fetchWorlds()
    }

    private fun fetchWorlds() {
        viewModelScope.launch {
            // 1. Poner la UI en estado de carga
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 2. Hacer la llamada a la API
                // Usamos language_id = 1 como indicaste
                // Usamos language_id = 1 como indicaste
                val languageId = 1 // Puedes obtener este valor de donde necesites
                val response = apiService.getAllWorlds(languageId = languageId) // Pasas el Int directamente

                if (response.isSuccessful && response.body() != null) {
                    val apiWorlds = response.body()!!.worlds
                    // 3. Convertir la respuesta de la API al modelo de la UI
                    val uiWorlds = apiWorlds.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(isLoading = false, worlds = uiWorlds)
                    }
                } else {
                    // Manejar error de la API
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al obtener los mundos"
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
 * Esto nos permite mantener la UI sin cambios y añadir los datos que la API no nos da,
 * como la imagen, los colores, etc.
 */
fun WorldInfoResponse.toUiModel(): World {
    // Aquí puedes personalizar cómo se ve cada mundo.
    // Usamos el ID para asignar imágenes y colores específicos.
    return World(
        id = this.id,
        name = this.name,
        description = this.description,
        detailedDescription = "Una nueva aventura te espera en el mundo de '${this.name}'. ¡Prepárate para aprender!",
        difficulty = (this.id % 5) + 1, // Dificultad basada en el ID (ej: 1, 2, 3, 4, 5, 1...)
        // Asignamos una imagen basado en el ID del mundo
        imageRes = when (this.id) {
            1 -> R.drawable.saludos // Asume que tienes este drawable
            2 -> R.drawable.animales // Asume que tienes este drawable
            3 -> R.drawable.casa // Asume que tienes este drawable
            4 -> R.drawable.colores
            else -> R.drawable.bosque
        },
        // Como indicaste, todos los mundos vienen desbloqueados
        isLocked = false,
        // Asignamos colores basados en el ID
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
        // Puedes poner valores por defecto para los demás campos
        completedLevels = 0,
        totalLevels = 10,
        xpReward = 100
    )
}