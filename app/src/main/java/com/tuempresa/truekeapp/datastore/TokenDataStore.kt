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

    suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }

    suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(TOKEN_KEY, null)
    }

    suspend fun isTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        val token = getToken() ?: return@withContext true

        try {
            val parts = token.split(".")
            if (parts.size != 3) return@withContext true

            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val payload = JSONObject(payloadJson)

            val exp = payload.getLong("exp")
            val now = System.currentTimeMillis() / 1000
            return@withContext now >= exp
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext true
        }
    }
}
