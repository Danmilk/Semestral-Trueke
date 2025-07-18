package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.tuempresa.truekeapp.R
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

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
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar cuenta")
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
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("¿Estás seguro?") },
                    text = { Text("Esta acción eliminará permanentemente tu cuenta y no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            scope.launch {
                                val response = repository.deleteAccount()
                                if (response.isSuccessful) {
                                    repository.logout()
                                    showSuccessDialog = true // 👈 Mostrar imagen del gato
                                } else {
                                    // Snackbar o log opcional
                                }
                            }
                        }) {
                            Text("Sí, eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
,
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessDialog = false
                        onLogout() // Redirige a Login después de cerrar el dialog
                    },
                    title = { Text("Cuenta eliminada ") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("¡Tu cuenta fue eliminada con éxito!")
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                painter = painterResource(id = R.drawable.cat_laughing),
                                contentDescription = "Gato riéndose",
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            onLogout()
                        }) {
                            Text("Cerrar")
                        }
                    }
                )
            }



        }
    }
}
