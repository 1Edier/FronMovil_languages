package com.mundosoft.frontjuego.network

import com.google.gson.annotations.SerializedName

// --- Modelos de Petición y Autenticación (SIN CAMBIOS) ---
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserInfo
)
data class UserInfo(@SerializedName("id") val id: Int, @SerializedName("username") val username: String)
data class RegisterResponse(@SerializedName("message") val message: String)

// --- ¡NUEVO MODELO PARA ERRORES DE API! ---
// Nos ayudará a parsear las respuestas de error que vienen en JSON
data class ApiErrorResponse(
    @SerializedName("message") val message: String
)

// --- Modelo para la petición de mundos (SIN CAMBIOS) ---
data class GetWorldsRequest(
    @SerializedName("language_id")
    val language_id: Int
)

// --- Modelos para la respuesta de /api/game/getWorlds (SIN CAMBIOS) ---
data class WorldsApiResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("worlds")
    val worlds: List<WorldInfoResponse>
)

data class WorldInfoResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String
)

// --- Modelos para GET /api/users/progress/{userId} (SIN CAMBIOS) ---
data class UserProgressApiResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("userProgress")
    val userProgress: List<UserProgressItem>
)

data class UserProgressItem(
    @SerializedName("level_id")
    val levelId: Int,
    @SerializedName("status")
    val status: String
)

// --- Modelos para GET /api/game/getLevelContent/{userId}/{levelId} (SIN CAMBIOS) ---
data class LevelContentApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: LevelData
)

data class LevelData(
    @SerializedName("level_id") val levelId: Int,
    @SerializedName("level_name") val levelName: String,
    @SerializedName("level_order") val levelOrder: Int,
    @SerializedName("exercises") val exercises: List<Exercise>
)

data class Exercise(
    @SerializedName("exercise_id") val exerciseId: Int,
    @SerializedName("exercise_type") val exerciseType: String,
    @SerializedName("question_or_instruction") val question: String,
    @SerializedName("options") val options: List<ExerciseOption>
)

data class ExerciseOption(
    @SerializedName("option_id") val optionId: Int,
    @SerializedName("is_correct") val isCorrect: Int, // API devuelve 1 para true, 0 para false
    @SerializedName("vocabulary") val vocabulary: Vocabulary
)

data class Vocabulary(
    @SerializedName("vocabulary_id") val vocabularyId: Int,
    @SerializedName("language_id") val languageId: Int,
    @SerializedName("native_word") val nativeWord: String,
    @SerializedName("foreign_word") val foreignWord: String,
    @SerializedName("image_url") val imageUrl: String? // Puede ser nulo
)


// --- MODELOS CORREGIDOS PARA LA RESPUESTA DE checkAnswer (SIN CAMBIOS) ---
data class CheckAnswerData(
    @SerializedName("message") val message: String,
    @SerializedName("score") val score: Int,
    @SerializedName("isCorrect") val isCorrect: Boolean
)

data class CheckAnswerApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: CheckAnswerData
)
data class FinishLevelRequest(
    @SerializedName("level_id") val levelId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("score") val score: Int
)

data class GenericApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

// --- MODELOS PARA EL PERFIL DE USUARIO (SIN CAMBIOS) ---
data class UserProfileResponse(
    @SerializedName("message") val message: String,
    @SerializedName("userProfile") val userProfile: UserProfileData
)

data class UserProfileData(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("learning_language_id") val learningLanguageId: Int,
    @SerializedName("difficulty_level") val difficultyLevel: String,
    @SerializedName("total_points") val totalPoints: Int
)

data class UserBadgesResponse(
    @SerializedName("message") val message: String,
    @SerializedName("userBadges") val userBadges: List<Badge>
)

data class Badge(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon_url") val iconUrl: String,
    @SerializedName("obtained_at") val obtainedAt: String
)