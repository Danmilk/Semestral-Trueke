// File: app/src/main/java/com/truekeapp/ui/components/BottomNavBar.kt
package com.tuempresa.truekeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.tuempresa.truekeapp.ui.navigation.Screen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Inicio", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Enviadas", Icons.Default.Send, Screen.OffersSent.route),
        BottomNavItem("Recibidas", Icons.Default.Inbox, Screen.OffersReceived.route),
        BottomNavItem("Cuenta", Icons.Default.Person, Screen.Account.route)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
