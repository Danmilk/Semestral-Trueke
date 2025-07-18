package com.tuempresa.truekeapp.datastore

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TokenDataStore(context: Context) {

    companion object {
        private const val FILE_NAME = "secure_token_prefs"
        private const val TOKEN_KEY = "jwt_token"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val TIMESTAMP_KEY = "token_timestamp"
    suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        sharedPreferences.edit()
            .putString(TOKEN_KEY, token)
            .putLong(TIMESTAMP_KEY, now)
            .apply()
    }

    suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(TIMESTAMP_KEY)
            .apply()
    }


    suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(TOKEN_KEY, null)
    }



    private val EXPIRATION_WINDOW_MILLIS = 5 * 60 * 1000 // 5 minutos

    suspend fun isTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext true

        val savedTime = sharedPreferences.getLong(TIMESTAMP_KEY, -1L)
        val now = System.currentTimeMillis()

        if (savedTime != -1L && now - savedTime > EXPIRATION_WINDOW_MILLIS) {
            return@withContext true
        }

        // Validación opcional con exp del JWT (puedes mantenerla si quieres)
        try {
            val parts = token.split(".")
            if (parts.size != 3) return@withContext true

            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val payload = JSONObject(payloadJson)

            val exp = payload.getLong("exp")
            val nowSeconds = now / 1000
            return@withContext nowSeconds >= exp
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext true
        }
    }

}
