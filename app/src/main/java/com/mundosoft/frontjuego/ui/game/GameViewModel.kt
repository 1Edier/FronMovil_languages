package com.mundosoft.frontjuego.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundosoft.frontjuego.network.FinishLevelRequest
import com.mundosoft.frontjuego.network.LevelData
import com.mundosoft.frontjuego.network.RetrofitClient
import com.mundosoft.frontjuego.util.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la respuesta del usuario para un ejercicio
enum class AnswerState {
    IDLE,      // Esperando respuesta
    CORRECT,   // Respuesta correcta
    INCORRECT  // Respuesta incorrecta
}

// Estado completo de la UI de la pantalla de juego
data class GameUiState(
    val isLoading: Boolean = true,
    val levelData: LevelData? = null,
    val currentExerciseIndex: Int = 0,
    val selectedOptionId: Int? = null, // Para multiple_choice
    val typedAnswer: String = "",       // Para fill_in_word
    val answerState: AnswerState = AnswerState.IDLE,
    val isLevelComplete: Boolean = false,
    val score: Int = 0,                 // Contador de puntos para el nivel
    val error: String? = null
)

class GameViewModel(private val levelId: Int) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance

    init {
        loadLevelContent()
    }

    private fun loadLevelContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = SessionManager.getUserId()
            if (userId == -1) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no encontrado.") }
                return@launch
            }

            try {
                val response = apiService.getLevelContent(userId, levelId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(isLoading = false, levelData = response.body()!!.data)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar el nivel.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onOptionSelected(optionId: Int) {
        if (_uiState.value.answerState == AnswerState.IDLE) {
            _uiState.update { it.copy(selectedOptionId = optionId) }
        }
    }

    fun onTextAnswerChanged(text: String) {
        if (_uiState.value.answerState == AnswerState.IDLE) {
            _uiState.update { it.copy(typedAnswer = text) }
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        val exercise = currentState.levelData?.exercises?.get(currentState.currentExerciseIndex) ?: return
        if (currentState.selectedOptionId == null && currentState.typedAnswer.isBlank()) return

        viewModelScope.launch {
            try {
                if (exercise.exerciseType == "multiple_choice" && currentState.selectedOptionId != null) {
                    val response = apiService.checkAnswer(exercise.exerciseId, currentState.selectedOptionId)
                    if (response.isSuccessful && response.body() != null) {
                        val responseData = response.body()!!.data
                        val isCorrect = responseData.isCorrect
                        val points = responseData.score

                        _uiState.update {
                            it.copy(
                                answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT,
                                score = it.score + points
                            )
                        }
                    } else {
                        _uiState.update { it.copy(error = "Error al verificar respuesta.") }
                    }
                } else if (exercise.exerciseType == "fill_in_word") {
                    // Lógica hardcodeada, idealmente la respuesta vendría de la API
                    val isCorrect = currentState.typedAnswer.equals("días", ignoreCase = true)
                    val points = if (isCorrect) 100 else -10
                    _uiState.update {
                        it.copy(
                            answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT,
                            score = it.score + points
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun proceedToNext() {
        val currentState = _uiState.value

        if (currentState.answerState == AnswerState.CORRECT) {
            val totalExercises = currentState.levelData?.exercises?.size ?: 0
            if (currentState.currentExerciseIndex < totalExercises - 1) {
                _uiState.update {
                    it.copy(
                        currentExerciseIndex = it.currentExerciseIndex + 1,
                        selectedOptionId = null,
                        typedAnswer = "",
                        answerState = AnswerState.IDLE
                    )
                }
            } else {
                markLevelAsComplete()
                _uiState.update { it.copy(isLevelComplete = true) }
            }
        } else if (currentState.answerState == AnswerState.INCORRECT) {
            _uiState.update {
                it.copy(
                    selectedOptionId = null,
                    typedAnswer = "",
                    answerState = AnswerState.IDLE
                )
            }
        }
    }

    // --- ¡FUNCIÓN MODIFICADA! ---
    private fun markLevelAsComplete() {
        viewModelScope.launch {
            val userId = SessionManager.getUserId()
            if (userId == -1) {
                println("Error: No se pudo enviar el progreso, userId no encontrado.")
                return@launch
            }

            val finalScore = _uiState.value.score
            val request = FinishLevelRequest(
                levelId = this@GameViewModel.levelId,
                status = "completed",
                score = finalScore
            )

            try {
                // Lanzamos ambas llamadas en paralelo para mayor eficiencia
                val deferredResponses = awaitAll(
                    async { apiService.finishLevel(userId, request) },
                    async { apiService.updateUserProgress(userId, request) }
                )

                // Verificamos el resultado de la primera llamada (finishLevel)
                val finishLevelResponse = deferredResponses[0]
                if (finishLevelResponse.isSuccessful) {
                    println("Progreso del nivel (finishLevel) guardado exitosamente: ${finishLevelResponse.body()?.message}")
                } else {
                    println("Error al guardar (finishLevel): ${finishLevelResponse.errorBody()?.string()}")
                }

                // Verificamos el resultado de la segunda llamada (updateUserProgress)
                val updateUserProgressResponse = deferredResponses[1]
                if (updateUserProgressResponse.isSuccessful) {
                    println("Progreso del usuario (progressUpdate) guardado exitosamente: ${updateUserProgressResponse.body()?.message}")
                } else {
                    println("Error al guardar (progressUpdate): ${updateUserProgressResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Excepción al guardar el progreso del nivel en uno de los endpoints: ${e.message}")
            }
        }
    }
}