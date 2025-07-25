package com.example.frontjuego.network

import com.example.frontjuego.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // USA ESTA IP PARA EL EMULADOR DE ANDROID STUDIO (se refiere al localhost de tu máquina)
    private const val BASE_URL = " https://25131d972974.ngrok-free.app"
    // Si usas un dispositivo físico, reemplaza 10.0.2.2 con la IP de tu PC en la red local.

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para añadir el token de autorización
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Obtenemos el token del SessionManager
        SessionManager.fetchAuthToken()?.let { token ->
            // Añadimos la cabecera "Authorization" con el Bearer Token
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }

    // Añadimos el nuevo interceptor al cliente OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor) // Interceptor de autenticación
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}