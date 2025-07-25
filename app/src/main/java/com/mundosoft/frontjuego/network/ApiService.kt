    package com.mundosoft.frontjuego.network

    import com.mundosoft.frontjuego.model.Level
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.PUT
    import retrofit2.http.Path

    interface ApiService {

        @POST("api/auth")
        suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

        @POST("api/users")
        suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

        // --- Juego (sin cambios) ---
        @GET("api/game/getWorlds/{languageId}")
        suspend fun getAllWorlds(@Path("languageId") languageId: Int): Response<WorldsApiResponse>

        @GET("api/game/getLevels/{worldId}")
        suspend fun getLevelsForWorld(@Path("worldId") worldId: Int): Response<List<Level>>

        @GET("api/game/getLevelContent/{userId}/{levelId}")
        suspend fun getLevelContent(
            @Path("userId") userId: Int,
            @Path("levelId") levelId: Int
        ): Response<LevelContentApiResponse>

        @GET("api/game/checkAnswer/{exerciseId}/{optionId}")
        suspend fun checkAnswer(
            @Path("exerciseId") exerciseId: Int,
            @Path("optionId") optionId: Int
        ): Response<CheckAnswerApiResponse>

        @PUT("api/game/finishLevel/{userId}")
        suspend fun finishLevel(
            @Path("userId") userId: Int,
            @Body request: FinishLevelRequest
        ): Response<GenericApiResponse>

        @PUT("api/users/progressUpdate/{userId}")
        suspend fun updateUserProgress(
            @Path("userId") userId: Int,
            @Body request: FinishLevelRequest
        ): Response<GenericApiResponse>


        // --- Progreso del Usuario ---
        @GET("api/users/progress/{userId}")
        suspend fun getUserProgress(@Path("userId") userId: Int): Response<UserProgressApiResponse>

        // --- ¡NUEVAS FUNCIONES DE PERFIL! ---
        @GET("api/users/profile/{userId}")
        suspend fun getUserProfile(@Path("userId") userId: Int): Response<UserProfileResponse>

        @GET("api/users/badges/{userId}")
        suspend fun getUserBadges(@Path("userId") userId: Int): Response<UserBadgesResponse>
    }