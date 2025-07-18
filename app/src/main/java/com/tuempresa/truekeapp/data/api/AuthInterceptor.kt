package com.tuempresa.truekeapp.data.api

import com.tuempresa.truekeapp.datastore.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        // Si es login o register, omite token
        if (url.contains("/login") || url.contains("/register")) {
            return chain.proceed(request)
        }

        return runBlocking {
            val token = tokenDataStore.getToken()

            if (token != null) {
                val isExpired = tokenDataStore.isTokenExpired()
                if (isExpired) {
                    tokenDataStore.clearToken()
                } else {
                    val newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    return@runBlocking chain.proceed(newRequest)
                }
            }

            return@runBlocking chain.proceed(request)
        }
    }
}

