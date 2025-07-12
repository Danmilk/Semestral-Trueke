package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersSentScreen(
    repository: TruekeRepository
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ofertas Enviadas") })
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.OffersSent.route,
                onNavigate = { /* Agrega navegación si gustas */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Esta funcionalidad aún no está disponible.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
