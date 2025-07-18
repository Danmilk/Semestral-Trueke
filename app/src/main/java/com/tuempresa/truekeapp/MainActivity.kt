// File: app/src/main/java/com/truekeapp/MainActivity.kt
package com.tuempresa.truekeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuempresa.truekeapp.data.api.RetrofitClient
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.datastore.TokenDataStore
import com.tuempresa.truekeapp.ui.navigation.TruekeNavGraph
import com.tuempresa.truekeapp.ui.theme.TruekeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa TokenDataStore
        val tokenDataStore = TokenDataStore(applicationContext)

        // Crea instancia de API con interceptor
        val api = RetrofitClient.create(tokenDataStore)

        // Crea repositorio
        val repository = TruekeRepository(api, tokenDataStore)

        setContent {
            TruekeTheme {
                TruekeNavGraph(repository = repository)
            }
        }
    }
}
