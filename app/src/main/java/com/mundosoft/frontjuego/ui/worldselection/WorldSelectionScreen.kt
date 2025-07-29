package com.mundosoft.frontjuego.ui.worldselection

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mundosoft.frontjuego.ui.navigation.Screen
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldSelectionScreen(
    navController: NavController,
    onWorldSelected: (worldId: Int, worldName: String) -> Unit,
    viewModel: WorldSelectionViewModel = viewModel()
) {
    var selectedWorld by remember { mutableStateOf<World?>(null) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()


    // Cuando vuelvas de completar un mundo, se volverá a ejecutar, recargando los datos.
    LaunchedEffect(key1 = true) {
        viewModel.loadWorlds()
    }


    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🌟 MUNDO IDIOMAS 🌟",
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil de Usuario",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2), Color(0xFF1E88E5), Color(0xFF42A5F5)
                            )
                        )
                    )
            ) {
                EnhancedBackgroundDecorations(waveOffset, pulseScale, shimmerOffset)

                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    uiState.error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = Color.Red,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "¡Ups! Algo salió mal",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = uiState.error!!,
                                        textAlign = TextAlign.Center,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp, bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            SelectedWorldInfoCard(selectedWorld)
                            WorldsGameSection(
                                worlds = uiState.worlds,
                                selectedWorld = selectedWorld,
                                onWorldSelected = { newSelection -> selectedWorld = newSelection }
                            )
                            GameConfirmationButton(
                                selectedWorld = selectedWorld,
                                onConfirm = {
                                    selectedWorld?.let {
                                        if (!it.isLocked) {
                                            onWorldSelected(it.id, it.name)
                                            Toast.makeText(context, "Cargando niveles de ${it.name}...", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SelectedWorldInfoCard(selectedWorld: World?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // Cambio: permite que la altura se ajuste al contenido
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Cambio: permite que la altura se ajuste al contenido
                .background(
                    if (selectedWorld != null) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                selectedWorld.backgroundColor.copy(alpha = 0.1f),
                                selectedWorld.backgroundColor.copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD),
                                Color(0xFFF3E5F5)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selectedWorld != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconToShow = if (selectedWorld.isLocked) Icons.Default.Lock else Icons.Default.Star
                        Icon(
                            imageVector = iconToShow,
                            contentDescription = null,
                            tint = selectedWorld.backgroundColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedWorld.name,
                            color = selectedWorld.backgroundColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2, // Cambio: permite hasta 2 líneas
                            textAlign = TextAlign.Center // Cambio: centrar el texto
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Cambio: un poco más de espacio
                    Text(
                        text = selectedWorld.description,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 3, // Cambio: permite hasta 3 líneas para la descripción
                        lineHeight = 18.sp // Cambio: ajusta el espaciado entre líneas
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp) // Cambio: agregar padding consistente
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🎮 Selecciona tu mundo para comenzar",
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 2 // Cambio: permite hasta 2 líneas
                    )
                }
            }
        }
    }
}
@Composable
fun EnhancedBackgroundDecorations(waveOffset: Float, pulseScale: Float, shimmerOffset: Float) {
    repeat(12) { index ->
        val offsetX = sin(Math.toRadians((waveOffset + index * 30).toDouble())).toFloat() * 80.dp.value
        val offsetY = cos(Math.toRadians((waveOffset + index * 20).toDouble())).toFloat() * 60.dp.value
        val particleSize = (6 + index % 5 * 3).dp
        Box(
            modifier = Modifier
                .offset(
                    x = (30 + index * 35).dp + offsetX.dp,
                    y = (80 + index * 50).dp + offsetY.dp
                )
                .size(particleSize)
                .scale(if (index % 3 == 0) pulseScale else 1f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color(0xFFFFD700).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = particleSize.value
                    ),
                    CircleShape
                )
        )
    }
    repeat(3) { index ->
        val waveY = 100 + index * 200
        val alpha = 0.1f - index * 0.03f
        Box(
            modifier = Modifier
                .offset(
                    x = (shimmerOffset * 0.5f + index * 100).dp,
                    y = waveY.dp
                )
                .size(80.dp)
                .graphicsLayer {
                    rotationZ = waveOffset + index * 120
                }
                .background(
                    Color(0xFF64B5F6).copy(alpha = alpha),
                    CircleShape
                )
        )
    }
}

@Composable
fun WorldsGameSection(
    worlds: List<World>,
    selectedWorld: World?,
    onWorldSelected: (World) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "🗺️ ELIGE TU DESTINO",
                color = Color(0xFF1565C0),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre items
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(worlds) { world ->
                GameWorldItem(
                    world = world,
                    isSelected = world.id == selectedWorld?.id,
                    onWorldClick = { onWorldSelected(world) }
                )
            }
        }
    }
}

@Composable
fun GameWorldItem(
    world: World,
    isSelected: Boolean,
    onWorldClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f, // Un poco más sutil
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 5f else 0f,
        animationSpec = tween(300),
        label = "rotationAnimation"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = tween(durationMillis = 300),
        label = "elevationAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .graphicsLayer { rotationZ = rotation }
            .clickable(enabled = !world.isLocked) { onWorldClick() }
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            modifier = Modifier.size(140.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Imagen de fondo
                Image(
                    painter = painterResource(id = world.imageRes),
                    contentDescription = world.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                )

                // Gradiente para mejorar la legibilidad del texto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                )

                // Nombre y dificultad (Parte inferior izquierda)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {

                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            repeat(world.difficulty) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Ícono de Play cuando está seleccionado
                if (isSelected && !world.isLocked) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = world.backgroundColor,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(12.dp)
                            )
                        }
                    }
                }

                // Overlay de bloqueo
                if (world.isLocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Black.copy(alpha = 0.7f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = world.name,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun GameConfirmationButton(
    selectedWorld: World?,
    onConfirm: () -> Unit
) {
    val isEnabled = selectedWorld != null && !selectedWorld.isLocked

    val buttonScale by animateFloatAsState(
        targetValue = if (isEnabled) 1.0f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "button")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEnabled) 16.dp else 4.dp
        )
    ) {
        Button(
            onClick = onConfirm,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedWorld != null) {
                    selectedWorld.backgroundColor
                } else {
                    Color.Gray.copy(alpha = 0.3f)
                },
                disabledContainerColor = if (selectedWorld != null && selectedWorld.isLocked) {
                    Color.Gray.copy(alpha = 0.8f) // Color más oscuro para bloqueado
                } else {
                    Color.Gray.copy(alpha = 0.3f)
                }
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    if (isEnabled) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                selectedWorld!!.backgroundColor,
                                selectedWorld.backgroundColor.copy(alpha = 0.8f),
                                selectedWorld.backgroundColor
                            ),
                            startX = shimmer * 1000f,
                            endX = shimmer * 1000f + 200f
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    },
                    RoundedCornerShape(30.dp)
                )
        ) {
            val buttonText: String
            val buttonIcon: androidx.compose.ui.graphics.vector.ImageVector

            when {
                selectedWorld == null -> {
                    buttonText = "📍 SELECCIONA UN MUNDO"
                    buttonIcon = Icons.Default.Explore
                }
                selectedWorld.isLocked -> {
                    buttonText = "🔒 MUNDO BLOQUEADO"
                    buttonIcon = Icons.Default.Lock
                }
                else -> {
                    buttonText = "🚀 COMENZAR AVENTURA"
                    buttonIcon = Icons.Default.PlayArrow
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}