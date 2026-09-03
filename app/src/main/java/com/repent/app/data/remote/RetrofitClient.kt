package com.repent.app.data.remote

import com.repent.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * Base URL for the PHP API. Defaults to BuildConfig.BASE_URL, which
     * points at the Android emulator's alias for the host machine
     * (10.0.2.2) so `php -S localhost:8000` on your dev machine "just
     * works" when running in the emulator. Change BASE_URL in
     * app/build.gradle.kts once you deploy the API to a real server.
     */
    private val baseUrl: String = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /** Helper to build the "Bearer <token>" header value. */
    fun bearer(token: String) = "Bearer $token"
}
