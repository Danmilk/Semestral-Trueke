package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onInventoryClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToHome: () -> Unit,
    onNavigateToSent: () -> Unit,
    onNavigateToReceived: () -> Unit,
    repository: TruekeRepository
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cuenta") })
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Account.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> onNavigateToHome()
                        Screen.OffersSent.route -> onNavigateToSent()
                        Screen.OffersReceived.route -> onNavigateToReceived()
                        // Ya estás en Cuenta
                    }
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Hola, bienvenido a tu cuenta",
                style = MaterialTheme.typography.titleLarge
            )

            // Botón 1: Ir al Inventario (funcional)
            Button(
                onClick = onInventoryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Inventory, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ver inventario")
            }

            // Botón 2: Configuración (placeholder)
            OutlinedButton(
                onClick = { /* aún no implementado */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Configuración (proximamente)")
            }

            // Botón 3: Logout
            Button(
                onClick = {
                    scope.launch {
                        repository.logout()
                        onLogout()  // Navegar al login
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}
