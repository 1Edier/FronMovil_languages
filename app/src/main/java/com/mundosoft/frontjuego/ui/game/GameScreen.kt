package com.mundosoft.frontjuego.ui.game

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mundosoft.frontjuego.R
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// --- FUNCIÓN DE AYUDA PARA REPRODUCIR SONIDOS ---
private fun playSound(context: Context, @RawRes soundResId: Int) {

    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setOnCompletionListener { mp ->
        mp.release()
    }
    mediaPlayer.start()
}

// Factory del ViewModel (sin cambios)
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
    val context = LocalContext.current // Obtenemos el contexto para los sonidos

    // --- Sonidos---

    LaunchedEffect(uiState.answerState) {
        when (uiState.answerState) {
            AnswerState.CORRECT -> playSound(context, R.raw.sound_correct)
            AnswerState.INCORRECT -> playSound(context, R.raw.sound_incorrect)
            AnswerState.IDLE -> { /* No hacer nada en el estado inicial */ }
        }
    }

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

    // --- MUESTRA LA ANIMACIÓN DE NIVEL COMPLETADO SI EL ESTADO ES ACTIVO ---
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

    // Efecto que se ejecuta una vez cuando aparece la animación
    LaunchedEffect(Unit) {
        isVisible = true
        delay(4000)
        onDismiss()
    }


    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(
                    onClick = onDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) { /* Este Box es solo para el fondo oscuro */ }
    }

    // Contenido principal de la animación
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Animación de confeti de fondo
            ConfettiAnimation()

            // Tarjeta con el mensaje de "Nivel Completado"
            Card(
                modifier = Modifier.padding(32.dp),
                shape = RoundedCornerShape(24.dp), // Corregido
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎉 ¡Nivel Completado! 🎉",
                        fontSize = 24.sp, // Corregido
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Puntuación: $score",
                        fontSize = 20.sp // Corregido
                    )
                }
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