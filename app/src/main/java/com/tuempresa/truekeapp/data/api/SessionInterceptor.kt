package com.tuempresa.truekeapp.data.api

import com.tuempresa.truekeapp.datastore.TokenDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor(
    private val tokenDataStore: TokenDataStore,
    private val onSessionExpired: () -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            CoroutineScope(Dispatchers.IO).launch {
                tokenDataStore.clearToken()
                onSessionExpired()
            }
        }

        return response
    }
}
