package com.mundosoft.frontjuego.network

import com.mundosoft.frontjuego.util.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    private const val BASE_URL = "https://brainlish.duckdns.org"

    // Creamos un interceptor para añadir el token de autenticación a cada petición
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()


        val token = SessionManager.getAuthToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            // Añadimos el encabezado de autorización si tenemos un token
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val newRequest = requestBuilder.build()
        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Creamos la instancia de Retrofit
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usamos el cliente con el interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}