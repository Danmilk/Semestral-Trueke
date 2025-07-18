package com.tuempresa.truekeapp.data.api

import com.google.gson.GsonBuilder
import com.tuempresa.truekeapp.datastore.TokenDataStore
import com.tuempresa.truekeapp.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "https://trueke-api-bgoo.onrender.com" // Ajusta a tu URL del servidor

    fun create(tokenDataStore: TokenDataStore): TruekeApi {
        val authInterceptor = AuthInterceptor(tokenDataStore)
        val sessionInterceptor = SessionInterceptor(tokenDataStore) {
            CoroutineScope(Dispatchers.Default).launch {
                SessionManager.emitSessionExpired()
            }
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(sessionInterceptor)
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TruekeApi::class.java)
    }
}