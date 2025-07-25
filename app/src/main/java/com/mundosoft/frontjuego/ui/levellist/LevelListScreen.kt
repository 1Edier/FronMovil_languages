package com.mundosoft.frontjuego.ui.levellist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Factory para poder pasar el worldId al ViewModel
class LevelListViewModelFactory(private val worldId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LevelListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LevelListViewModel(worldId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelListScreen(
    worldId: Int,
    worldName: String,
    navController: NavController,
    onLevelSelected: (Int) -> Unit
) {
    val viewModel: LevelListViewModel = viewModel(factory = LevelListViewModelFactory(worldId))
    val uiState by viewModel.uiState.collectAsState()

    // --- ¡CAMBIO IMPORTANTE AQUÍ! ---
    // Este `LaunchedEffect` se ejecutará cada vez que esta pantalla se muestre.
    // Cuando vuelvas de GameScreen, se volverá a ejecutar, recargando los datos.
    // La clave `true` asegura que se ejecute al menos una vez y en recomposiciones
    // si la pantalla sale y vuelve a entrar en el stack.
    LaunchedEffect(key1 = true) {
        viewModel.loadLevelsAndProgress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(worldName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF))
                    )
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.levels) { level ->
                            LevelItem(level = level, onClick = {
                                if (level.status != LevelStatus.LOCKED) {
                                    onLevelSelected(level.id)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LevelItem(level: UiLevel, onClick: () -> Unit) {
    val isLocked = level.status == LevelStatus.LOCKED
    val cardColor: Color
    val icon: ImageVector
    val iconColor: Color
    val iconBackgroundColor: Color

    when (level.status) {
        LevelStatus.COMPLETED -> {
            cardColor = Color(0xFF4CAF50)
            icon = Icons.Default.Check
            iconColor = Color.White
            iconBackgroundColor = Color(0xFF388E3C)
        }
        LevelStatus.UNLOCKED -> {
            cardColor = Color(0xFF2196F3)
            icon = Icons.Default.PlayArrow
            iconColor = Color.White
            iconBackgroundColor = Color(0xFF1976D2)
        }
        LevelStatus.LOCKED -> {
            cardColor = Color(0xFF9E9E9E)
            icon = Icons.Default.Lock
            iconColor = Color(0xFF616161)
            iconBackgroundColor = Color(0xFFBDBDBD)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = if (isLocked) 0.6f else 1.0f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = level.status.name,
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nivel ${level.sortOrder}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = level.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!isLocked) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ir al nivel",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}