package com.mundosoft.frontjuego.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable // <--- Add this import
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlin.math.sin

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.layout.Box




import androidx.compose.ui.unit.dp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

// Factory (sin cambios)
class GameViewModelFactory(private val levelId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(levelId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    levelId: Int,
    navController: NavController
) {
    val viewModel: GameViewModel = viewModel(factory = GameViewModelFactory(levelId))
    val uiState by viewModel.uiState.collectAsState()

    val currentExercise = uiState.levelData?.exercises?.getOrNull(uiState.currentExerciseIndex)
    val progress = if (uiState.levelData?.exercises?.isNotEmpty() == true) {
        (uiState.currentExerciseIndex).toFloat() / uiState.levelData!!.exercises.size.toFloat()
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(500), label = "progress")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.levelData?.levelName ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Salir del nivel")
                    }
                },
                actions = {
                    val totalExercises = uiState.levelData?.exercises?.size ?: 0
                    if (totalExercises > 0) {
                        Text(
                            text = "${(uiState.currentExerciseIndex + 1).coerceAtMost(totalExercises)} / $totalExercises",
                            modifier = Modifier.padding(end = 16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (currentExercise != null) {
                GameBottomBar(
                    answerState = uiState.answerState,
                    isAnswerSelected = uiState.selectedOptionId != null || uiState.typedAnswer.isNotBlank(),
                    onCheck = { viewModel.checkAnswer() },
                    onContinue = { viewModel.proceedToNext() }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text(
                            text = "Error: ${uiState.error}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    currentExercise != null -> {
                        when (currentExercise.exerciseType) {
                            "multiple_choice" -> MultipleChoiceExercise(
                                exercise = currentExercise,
                                selectedOptionId = uiState.selectedOptionId,
                                answerState = uiState.answerState,
                                onOptionSelected = viewModel::onOptionSelected
                            )
                            "fill_in_word" -> FillInTheWordExercise(
                                exercise = currentExercise,
                                typedAnswer = uiState.typedAnswer,
                                onTextAnswerChanged = viewModel::onTextAnswerChanged
                            )
                            else -> Text(
                                "Tipo de ejercicio no soportado: ${currentExercise.exerciseType}",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    // --- ¡NUEVA ANIMACIÓN DE NIVEL COMPLETADO! ---
    if (uiState.isLevelComplete) {
        LevelCompleteAnimation(
            score = uiState.score,
            onDismiss = {
                navController.popBackStack()
            }
        )
    }
}


@Composable
fun LevelCompleteAnimation(score: Int, onDismiss: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(4000) // Duración total de la animación antes de cerrar automáticamente
        onDismiss()
    }

    // Fondo semi-transparente que aparece gradualmente
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) { /* Lambda de contenido explícita y vacía */ } // <--- AÑADE ESTAS LLAVES VACÍAS
    }

    // Contenido animado (tarjeta y confeti)
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(contentAlignment = Alignment.Center) { // Este Box ya tiene contenido
            ConfettiAnimation()
            Card(
                // ... el resto de tu código de Card
            ) {
                // ... el resto de tu código de Column
            }
        }
    }
}

@Composable
fun ConfettiAnimation() {
    val particles = remember { List(100) { Particle() } }
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_progress"
    )

    val density = LocalDensity.current.density
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasHeight = size.height
        val canvasWidth = size.width

        particles.forEach { particle ->
            val currentY = (canvasHeight * (1 - progress) * particle.speed) - (canvasHeight * (1 - particle.startY))
            val currentX = particle.startX * canvasWidth + sin(currentY * 0.1f) * 50f * particle.sinAmplitude

            if (currentY < canvasHeight) {
                drawCircle(
                    color = particle.color,
                    radius = particle.size * density,
                    center = Offset(currentX, currentY)
                )
            }
        }
    }
}

private data class Particle(
    val color: Color = listOf(Color(0xFFf44336), Color(0xFF9c27b0), Color(0xFF2196f3), Color(0xFF4caf50), Color(0xFFffeb3b), Color(0xFFff9800)).random(),
    val size: Float = Random.nextFloat() * 4f + 2f,
    val startY: Float = Random.nextFloat(),
    val startX: Float = Random.nextFloat(),
    val speed: Float = Random.nextFloat() * 0.5f + 0.5f,
    val sinAmplitude: Float = Random.nextFloat() * 2f - 1f
)