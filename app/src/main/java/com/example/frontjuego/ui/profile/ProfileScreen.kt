package com.example.frontjuego.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontjuego.R
import com.example.frontjuego.network.Badge
import com.example.frontjuego.ui.navigation.Screen
import com.example.frontjuego.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel() // Inyectamos el ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = Color(0xFF4CAF50)
    val secondaryColor = Color(0xFF2196F3)
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = primaryColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    // Contenido cargado, mostramos el perfil
                    ProfileContent(
                        navController = navController,
                        username = uiState.username,
                        totalPoints = uiState.profile?.totalPoints,
                        badges = uiState.badges,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    navController: NavController,
    username: String?,
    totalPoints: Int?,
    badges: List<Badge>,
    primaryColor: Color,
    secondaryColor: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección de la Cabecera del Perfil
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bosque), // Imagen local
                        contentDescription = "Avatar de usuario",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(4.dp, Brush.radialGradient(listOf(primaryColor, secondaryColor)), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = username ?: "Jugador",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    // El email no viene en la API, por lo que se omite.
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección de Estadísticas
        item {
            Text("Mis Estadísticas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primaryColor)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Puntos Totales", (totalPoints ?: 0).toString(), Icons.Default.Star, secondaryColor)
                StatCard("Insignias Ganadas", badges.size.toString(), Icons.Default.EmojiEvents, secondaryColor)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección de Insignias
        item {
            Text("Mis Insignias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primaryColor)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (badges.isEmpty()) {
            item {
                NoBadgesMessage()
            }
        } else {
            // Ordenamos las insignias alfabéticamente por nombre
            val sortedBadges = badges.sortedBy { it.name }

            // Mostramos las insignias en una cuadrícula de 2 columnas con tamaño fijo
            items(sortedBadges.chunked(2)) { badgeRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    badgeRow.forEach { badge ->
                        BadgeItem(
                            name = badge.name,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 60.dp) // Altura mínima fija
                        )
                    }
                    // Si hay solo una insignia en la fila, agregamos un spacer para balancear
                    if (badgeRow.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Botón de Cerrar Sesión
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    SessionManager.clearSession() // Limpiamos la sesión al salir
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NoBadgesMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Sin insignias",
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Aún no tienes insignias!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sigue jugando y completando niveles para ganar tu primera insignia. ¡Tú puedes!",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.size(150.dp, 120.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray)
        }
    }
}

@Composable
fun BadgeItem(name: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Insignia",
                tint = Color(0xFFFFC107), // Color dorado
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}