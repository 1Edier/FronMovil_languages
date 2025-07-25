package com.mundosoft.frontjuego.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class AuthMode {
    LOGIN, REGISTER
}

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }//correo
    var password by remember { mutableStateOf("") }//contraseña
    var name by remember { mutableStateOf("") }//user registro
    var passwordVisible by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF4CAF50)
    val secondaryColor = Color(0xFF2196F3)
    val accentColor = Color(0xFFFF9800)
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White

    // Reaccionar a los cambios de estado (éxito o error)
    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            Toast.makeText(context, "¡Bienvenido! 🎉", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            authViewModel.resetStatus()
        }
    }
    LaunchedEffect(key1 = uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            // Ya no mostramos Toast aquí, el mensaje se muestra en la UI.
            // Cambiamos al modo login para que el usuario pueda entrar.
            authMode = AuthMode.LOGIN
            // No reseteamos aquí para que el mensaje de éxito siga visible.
            // Se reseteará al cambiar de modo o al intentar otra acción.
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.1f),
                        secondaryColor.copy(alpha = 0.05f),
                        backgroundColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🇺🇸 🇪🇸 🇫🇷 🇩🇪 🇮🇹 🇯🇵",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = if (authMode == AuthMode.LOGIN) "¡Aprende Idiomas Jugando!" else "¡Únete a la Aventura!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (authMode == AuthMode.LOGIN) "Domina nuevos idiomas de forma divertida" else "Crea tu cuenta y comienza a aprender",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (authMode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre", tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor,
                                cursorColor = primaryColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = secondaryColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = secondaryColor,
                            focusedLabelColor = secondaryColor,
                            cursorColor = secondaryColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña", tint = accentColor) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = accentColor
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor, strokeWidth = 3.dp)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (authMode == AuthMode.LOGIN) {
                                    authViewModel.login(email, password)
                                } else {
                                    authViewModel.register(name, email, password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (authMode == AuthMode.LOGIN) primaryColor else secondaryColor),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (authMode == AuthMode.LOGIN) " Iniciar Sesión" else " Registrarse",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // --- ¡SECCIÓN DE MENSAJES DE ESTADO MEJORADA! ---
                    // Mensaje de Error
                    uiState.error?.let { error ->
                        StatusMessageCard(
                            message = error,
                            isError = true
                        )
                    }

                    // Mensaje de Éxito
                    uiState.successMessage?.let { success ->
                        StatusMessageCard(
                            message = success,
                            isError = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para cambiar de modo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                TextButton(
                    onClick = {
                        authMode = if (authMode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
                        authViewModel.resetStatus() // Limpia cualquier mensaje al cambiar de modo
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (authMode == AuthMode.LOGIN) "¿Nuevo aquí? 🌟 Crea tu cuenta" else "¿Ya tienes cuenta? 🎯 Inicia Sesión",
                        color = if (authMode == AuthMode.LOGIN) secondaryColor else primaryColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "📚 Juegos interactivos • 🏆 Seguimiento de progreso",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Un Composable reutilizable para mostrar mensajes de estado (éxito o error).
 */
@Composable
fun StatusMessageCard(message: String, isError: Boolean) {
    val backgroundColor = if (isError) Color.Red.copy(alpha = 0.1f) else Color(0xFF4CAF50).copy(alpha = 0.15f)
    val icon = if (isError) Icons.Default.Warning else Icons.Default.CheckCircle
    val iconColor = if (isError) Color.Red else Color(0xFF388E3C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = iconColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}