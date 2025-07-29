package com.mundosoft.frontjuego.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundosoft.frontjuego.network.*
import com.mundosoft.frontjuego.util.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (AnswerState y GameUiState no cambian)
enum class AnswerState { IDLE, CORRECT, INCORRECT }
data class GameUiState(
    val isLoading: Boolean = true,
    val levelData: LevelData? = null,
    val currentExerciseIndex: Int = 0,
    val selectedOptionId: Int? = null,
    val typedAnswer: String = "",
    val answerState: AnswerState = AnswerState.IDLE,
    val isLevelComplete: Boolean = false,
    val score: Int = 0,
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
                    var finalLevelData = response.body()!!.data
                    val ID_NIVEL_FAMILY = 7
                    val ID_NIVEL_FOOD_L1 = 8
                    val ID_NIVEL_FOOD_L2 = 9
                    val ID_NIVEL_NUMBERS_L1 = 10
                    val ID_NIVEL_NUMBERS_L2 = 11
                    val ID_NIVEL_GREETINGS_L1 = 12

                    if (this@GameViewModel.levelId == ID_NIVEL_FAMILY) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsHermano = listOf(
                                ExerciseOption(optionId = 9001, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hermano", foreignWord = "Brother", imageUrl = "https://placehold.co/64x64.png?text=B")),
                                ExerciseOption(optionId = 9002, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hermana", foreignWord = "Sister", imageUrl = "https://placehold.co/64x64.png?text=S")),
                                ExerciseOption(optionId = 9003, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Padre", foreignWord = "Father", imageUrl = "https://placehold.co/64x64.png?text=F")),
                                ExerciseOption(optionId = 9004, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Madre", foreignWord = "Mother", imageUrl = "https://placehold.co/64x64.png?text=M"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsHermano)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsHermana = listOf(
                                ExerciseOption(optionId = 9005, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Padre", foreignWord = "Father", imageUrl = "https://placehold.co/64x64.png?text=F")),
                                ExerciseOption(optionId = 9006, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hermana", foreignWord = "Sister", imageUrl = "https://placehold.co/64x64.png?text=S")),
                                ExerciseOption(optionId = 9007, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hermano", foreignWord = "Brother", imageUrl = "https://placehold.co/64x64.png?text=B")),
                                ExerciseOption(optionId = 9008, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Madre", foreignWord = "Mother", imageUrl = "https://placehold.co/64x64.png?text=M"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsHermana)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }
                    else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L1) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsManzana = listOf(
                                ExerciseOption(optionId = 9009, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Manzana", foreignWord = "Apple", imageUrl = "https://placehold.co/64x64.png?text=A")),
                                ExerciseOption(optionId = 9010, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Naranja", foreignWord = "Orange", imageUrl = "https://placehold.co/64x64.png?text=O")),
                                ExerciseOption(optionId = 9011, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Plátano", foreignWord = "Banana", imageUrl = "https://placehold.co/64x64.png?text=B")),
                                ExerciseOption(optionId = 9012, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Uva", foreignWord = "Grape", imageUrl = "https://placehold.co/64x64.png?text=G"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsManzana)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsPan = listOf(
                                ExerciseOption(optionId = 9013, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Queso", foreignWord = "Cheese", imageUrl = "https://placehold.co/64x64.png?text=C")),
                                ExerciseOption(optionId = 9014, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Pan", foreignWord = "Bread", imageUrl = "https://placehold.co/64x64.png?text=Br")),
                                ExerciseOption(optionId = 9015, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Leche", foreignWord = "Milk", imageUrl = "https://placehold.co/64x64.png?text=M")),
                                ExerciseOption(optionId = 9016, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Huevo", foreignWord = "Egg", imageUrl = "https://placehold.co/64x64.png?text=E"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsPan)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }
                    else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L2) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsAgua = listOf(
                                ExerciseOption(optionId = 9017, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Agua", foreignWord = "Water", imageUrl = "https://placehold.co/64x64.png?text=W")),
                                ExerciseOption(optionId = 9018, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Jugo", foreignWord = "Juice", imageUrl = "https://placehold.co/64x64.png?text=J")),
                                ExerciseOption(optionId = 9019, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Vino", foreignWord = "Wine", imageUrl = "https://placehold.co/64x64.png?text=Wi")),
                                ExerciseOption(optionId = 9020, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Café", foreignWord = "Coffee", imageUrl = "https://placehold.co/64x64.png?text=C"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsAgua)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsLeche = listOf(
                                ExerciseOption(optionId = 9021, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Té", foreignWord = "Tea", imageUrl = "https://placehold.co/64x64.png?text=T")),
                                ExerciseOption(optionId = 9022, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Leche", foreignWord = "Milk", imageUrl = "https://placehold.co/64x64.png?text=M")),
                                ExerciseOption(optionId = 9023, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Agua", foreignWord = "Water", imageUrl = "https://placehold.co/64x64.png?text=W")),
                                ExerciseOption(optionId = 9024, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Cerveza", foreignWord = "Beer", imageUrl = "https://placehold.co/64x64.png?text=B"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsLeche)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }
                    else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L1) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsUno = listOf(
                                ExerciseOption(optionId = 9025, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Uno", foreignWord = "One", imageUrl = "https://placehold.co/64x64.png?text=1")),
                                ExerciseOption(optionId = 9026, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Dos", foreignWord = "Two", imageUrl = "https://placehold.co/64x64.png?text=2")),
                                ExerciseOption(optionId = 9027, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Tres", foreignWord = "Three", imageUrl = "https://placehold.co/64x64.png?text=3")),
                                ExerciseOption(optionId = 9028, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Cuatro", foreignWord = "Four", imageUrl = "https://placehold.co/64x64.png?text=4"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsUno)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsDos = listOf(
                                ExerciseOption(optionId = 9029, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Cinco", foreignWord = "Five", imageUrl = "https://placehold.co/64x64.png?text=5")),
                                ExerciseOption(optionId = 9030, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Seis", foreignWord = "Six", imageUrl = "https://placehold.co/64x64.png?text=6")),
                                ExerciseOption(optionId = 9031, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Dos", foreignWord = "Two", imageUrl = "https://placehold.co/64x64.png?text=2")),
                                ExerciseOption(optionId = 9032, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Siete", foreignWord = "Seven", imageUrl = "https://placehold.co/64x64.png?text=7"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsDos)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }
                    else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L2) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsTres = listOf(
                                ExerciseOption(optionId = 9033, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Ocho", foreignWord = "Eight", imageUrl = "https://placehold.co/64x64.png?text=8")),
                                ExerciseOption(optionId = 9034, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Nueve", foreignWord = "Nine", imageUrl = "https://placehold.co/64x64.png?text=9")),
                                ExerciseOption(optionId = 9035, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Diez", foreignWord = "Ten", imageUrl = "https://placehold.co/64x64.png?text=10")),
                                ExerciseOption(optionId = 9036, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Tres", foreignWord = "Three", imageUrl = "https://placehold.co/64x64.png?text=3"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsTres)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsCuatro = listOf(
                                ExerciseOption(optionId = 9037, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Ocho", foreignWord = "Eight", imageUrl = "https://placehold.co/64x64.png?text=8")),
                                ExerciseOption(optionId = 9038, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Cuatro", foreignWord = "Four", imageUrl = "https://placehold.co/64x64.png?text=4")),
                                ExerciseOption(optionId = 9039, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Nueve", foreignWord = "Nine", imageUrl = "https://placehold.co/64x64.png?text=9")),
                                ExerciseOption(optionId = 9040, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Diez", foreignWord = "Ten", imageUrl = "https://placehold.co/64x64.png?text=10"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsCuatro)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }
                    else if (this@GameViewModel.levelId == ID_NIVEL_GREETINGS_L1) {
                        val mutableExercises = finalLevelData.exercises.toMutableList()
                        if (mutableExercises.size > 0) {
                            val optionsHola = listOf(
                                ExerciseOption(optionId = 9041, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hola", foreignWord = "Hello", imageUrl = "https://placehold.co/64x64.png?text=H")),
                                ExerciseOption(optionId = 9042, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Adiós", foreignWord = "Goodbye", imageUrl = "https://placehold.co/64x64.png?text=G")),
                                ExerciseOption(optionId = 9043, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Gracias", foreignWord = "Thanks", imageUrl = "https://placehold.co/64x64.png?text=T")),
                                ExerciseOption(optionId = 9044, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Lo siento", foreignWord = "Sorry", imageUrl = "https://placehold.co/64x64.png?text=S"))
                            )
                            mutableExercises[0] = mutableExercises[0].copy(options = optionsHola)
                        }
                        if (mutableExercises.size > 1) {
                            val optionsAdios = listOf(
                                ExerciseOption(optionId = 9045, isCorrect = 1, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Adiós", foreignWord = "Goodbye", imageUrl = "https://placehold.co/64x64.png?text=G")),
                                ExerciseOption(optionId = 9046, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Hola", foreignWord = "Hello", imageUrl = "https://placehold.co/64x64.png?text=H")),
                                ExerciseOption(optionId = 9047, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Por favor", foreignWord = "Please", imageUrl = "https://placehold.co/64x64.png?text=P")),
                                ExerciseOption(optionId = 9048, isCorrect = 0, vocabulary = Vocabulary(vocabularyId = 0, languageId = 0, nativeWord = "Buenos días", foreignWord = "Good morning", imageUrl = "https://placehold.co/64x64.png?text=GM"))
                            )
                            mutableExercises[1] = mutableExercises[1].copy(options = optionsAdios)
                        }
                        finalLevelData = finalLevelData.copy(exercises = mutableExercises)
                    }

                    _uiState.update { it.copy(isLoading = false, levelData = finalLevelData) }
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
            val ID_NIVEL_CAT = 1
            val ID_DEL_NIVEL_SUN = 5
            val ID_NIVEL_FAMILY = 7
            val ID_NIVEL_FOOD_L1 = 8
            val ID_NIVEL_FOOD_L2 = 9
            val ID_NIVEL_NUMBERS_L1 = 10
            val ID_NIVEL_NUMBERS_L2 = 11
            val ID_NIVEL_GREETINGS_L1 = 12

            if (this@GameViewModel.levelId == ID_NIVEL_CAT && currentState.currentExerciseIndex == 2) {
                val isCorrect = currentState.typedAnswer.equals("cat", ignoreCase = true)
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_DEL_NIVEL_SUN && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.typedAnswer.equals("yellow", ignoreCase = true)
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FAMILY && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9001
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FAMILY && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9006
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L1 && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9009
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L1 && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9014
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L2 && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9017
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_FOOD_L2 && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9022
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L1 && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9025
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L1 && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9031
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L2 && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9036
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_NUMBERS_L2 && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9038
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_GREETINGS_L1 && currentState.currentExerciseIndex == 0) {
                val isCorrect = currentState.selectedOptionId == 9041
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            } else if (this@GameViewModel.levelId == ID_NIVEL_GREETINGS_L1 && currentState.currentExerciseIndex == 1) {
                val isCorrect = currentState.selectedOptionId == 9045
                _uiState.update { it.copy(answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrect) 100 else 0) }
                return@launch
            }

            try {
                if (exercise.exerciseType == "multiple_choice" && currentState.selectedOptionId != null) {
                    val response = apiService.checkAnswer(exercise.exerciseId, currentState.selectedOptionId)
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.update { it.copy(answerState = if (response.body()!!.data.isCorrect) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + response.body()!!.data.score) }
                    } else {
                        _uiState.update { it.copy(error = "Error al verificar respuesta.") }
                    }
                } else if (exercise.exerciseType == "fill_in_word") {
                    val isCorrectHardcoded = currentState.typedAnswer.equals("días", ignoreCase = true)
                    _uiState.update { it.copy(answerState = if (isCorrectHardcoded) AnswerState.CORRECT else AnswerState.INCORRECT, score = it.score + if (isCorrectHardcoded) 100 else -10) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun proceedToNext() {
        val currentState = _uiState.value
        val exercises = currentState.levelData?.exercises ?: return
        if (currentState.answerState == AnswerState.CORRECT) {
            var nextIndex = currentState.currentExerciseIndex + 1
            while (nextIndex < exercises.size && exercises[nextIndex].exerciseType == "matching") {
                _uiState.update { it.copy(score = it.score + 50) }
                nextIndex++
            }
            if (nextIndex >= exercises.size) {
                markLevelAsComplete()
                _uiState.update { it.copy(isLevelComplete = true) }
            } else {
                _uiState.update { it.copy(currentExerciseIndex = nextIndex, selectedOptionId = null, typedAnswer = "", answerState = AnswerState.IDLE) }
            }
        } else if (currentState.answerState == AnswerState.INCORRECT) {
            _uiState.update { it.copy(selectedOptionId = null, typedAnswer = "", answerState = AnswerState.IDLE) }
        }
    }


    // actualiza el puntaje local en SessionManager
    private fun markLevelAsComplete() {
        viewModelScope.launch {
            val userId = SessionManager.getUserId()
            if (userId == -1) { return@launch }
            val finalScore = _uiState.value.score

            SessionManager.addPoints(finalScore)


            println("PUNTOS_DEBUG: Enviando ${finalScore} puntos al servidor y sumando al total local.")

            val request = FinishLevelRequest(levelId = this@GameViewModel.levelId, status = "completed", score = finalScore)
            try {
                awaitAll(
                    async { apiService.finishLevel(userId, request) },
                    async { apiService.updateUserProgress(userId, request) }
                )
            } catch (e: Exception) {
                println("PUNTOS_DEBUG: Excepción al guardar el progreso del nivel: ${e.message}")
            }
        }
    }
}