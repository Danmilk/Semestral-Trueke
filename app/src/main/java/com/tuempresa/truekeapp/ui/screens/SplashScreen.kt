package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    repository: TruekeRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(1000) // pequeña pausa para que se vea el splash
            val token = repository.getToken()
            val expired = repository.isTokenExpired()

            if (token != null && !expired) {
                onNavigateToHome()
            } else {
                repository.logout() // elimina token si está expirado
                onNavigateToLogin()
            }
        }
    }

    // UI del splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text("Cargando sesión...")
        }
    }
}
