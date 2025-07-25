package com.mundosoft.frontjuego.ui.game

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mundosoft.frontjuego.network.Exercise
import java.util.Locale

@Composable
fun MultipleChoiceExercise(
    exercise: Exercise,
    selectedOptionId: Int?,
    answerState: AnswerState,
    onOptionSelected: (Int) -> Unit
) {
    val context = LocalContext.current

    // Corregir la inicialización de TextToSpeech
    val textToSpeech = remember {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Ahora podemos usar 'tts' de forma segura
                tts?.language = Locale.US
            }
        }
        tts
    }

    // Limpieza de recursos cuando el Composable se destruye
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.question,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val isAnswered = answerState != AnswerState.IDLE

        exercise.options.forEach { option ->
            val isSelected = option.optionId == selectedOptionId

            val (borderColor, backgroundColor) = when {
                isAnswered && option.isCorrect == 1 -> Color(0xFF4CAF50) to Color(0xFF4CAF50).copy(alpha = 0.2f)
                isAnswered && isSelected && option.isCorrect == 0 -> Color(0xFFD32F2F) to Color(0xFFD32F2F).copy(alpha = 0.2f)
                isSelected && !isAnswered -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> Color.Gray.copy(alpha = 0.5f) to Color.Transparent
            }

            val animatedBorderColor by animateColorAsState(targetValue = borderColor, label = "border")
            val animatedBackgroundColor by animateColorAsState(targetValue = backgroundColor, label = "background")

            OutlinedButton(
                onClick = {
                    if (!isAnswered) {
                        // Usar textToSpeech de forma segura
                        textToSpeech?.speak(
                            option.vocabulary.foreignWord,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        onOptionSelected(option.optionId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, animatedBorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = animatedBackgroundColor
                ),
                enabled = !isAnswered
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = option.vocabulary.imageUrl,
                        contentDescription = option.vocabulary.foreignWord,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 12.dp)
                    )
                    Text(text = option.vocabulary.foreignWord, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun FillInTheWordExercise(
    exercise: Exercise,
    typedAnswer: String,
    onTextAnswerChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.question,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = typedAnswer,
            onValueChange = onTextAnswerChanged,
            label = { Text("Tu respuesta") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GameBottomBar(
    answerState: AnswerState,
    isAnswerSelected: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit
) {
    val (barColor, message, buttonText, buttonColor, buttonEnabled) = when (answerState) {
        AnswerState.IDLE -> {
            Triple(
                MaterialTheme.colorScheme.surface,
                "",
                "VERIFICAR"
            ).let { (c, m, t) ->
                Tuple5(c, m, t, MaterialTheme.colorScheme.primary, isAnswerSelected)
            }
        }
        AnswerState.CORRECT -> {
            Triple(
                Color(0xFF4CAF50),
                "¡Excelente!",
                "CONTINUAR"
            ).let { (c, m, t) ->
                Tuple5(c, m, t, Color.White, true)
            }
        }
        AnswerState.INCORRECT -> {
            Triple(
                Color(0xFFD32F2F),
                "Respuesta incorrecta",
                "INTÉNTALO DE NUEVO"
            ).let { (c, m, t) ->
                Tuple5(c, m, t, Color.White, true)
            }
        }
    }

    val animatedBarColor by animateColorAsState(targetValue = barColor, animationSpec = tween(500), label = "barColor")
    val contentColor = if (answerState == AnswerState.IDLE) MaterialTheme.colorScheme.onPrimary else barColor

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = animatedBarColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (answerState != AnswerState.IDLE) {
                Text(
                    text = message,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = { if (answerState == AnswerState.IDLE) onCheck() else onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = buttonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = contentColor,
                    disabledContainerColor = buttonColor.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)