package com.example.frontjuego.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontjuego.ui.auth.AuthScreen
import com.example.frontjuego.ui.game.GameScreen // <-- AÑADIR IMPORTACIÓN
import com.example.frontjuego.ui.levellist.LevelListScreen
import com.example.frontjuego.ui.profile.ProfileScreen
import com.example.frontjuego.ui.worldselection.WorldSelectionScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object WorldSelection : Screen("world_selection_screen")
    object Profile : Screen("profile_screen")
    object LevelList : Screen("level_list_screen/{worldId}/{worldName}")
    // <-- AÑADIR NUEVA RUTA DE JUEGO -->
    object Game : Screen("game_screen/{levelId}")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        // ... (composable para Auth y WorldSelection sin cambios) ...

        composable(route = Screen.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.WorldSelection.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.WorldSelection.route) {
            WorldSelectionScreen(
                navController = navController,
                onWorldSelected = { worldId, worldName ->
                    navController.navigate("level_list_screen/$worldId/$worldName")
                }
            )
        }

        composable(
            route = Screen.LevelList.route,
            arguments = listOf(
                navArgument("worldId") { type = NavType.IntType },
                navArgument("worldName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val worldId = backStackEntry.arguments?.getInt("worldId")
            val worldName = backStackEntry.arguments?.getString("worldName")

            if (worldId != null && worldName != null) {
                LevelListScreen(
                    worldId = worldId,
                    worldName = worldName,
                    navController = navController,
                    // <-- ¡CAMBIO IMPORTANTE AQUÍ! -->
                    onLevelSelected = { levelId ->
                        // Navegamos a la pantalla de juego
                        navController.navigate("game_screen/$levelId")
                    }
                )
            }
        }

        // <-- AÑADIR NUEVO COMPOSABLE PARA LA PANTALLA DE JUEGO -->
        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId")
            if (levelId != null) {
                GameScreen(
                    levelId = levelId,
                    navController = navController
                )
            }
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}