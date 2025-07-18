package com.tuempresa.truekeapp.ui.navigation

import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tuempresa.truekeapp.data.model.ItemSharedViewModel
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.data.model.toItem
import com.tuempresa.truekeapp.session.SessionManager
import com.tuempresa.truekeapp.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object OffersSent : Screen("offers_sent")
    object OffersReceived : Screen("offers_received")
    object Account : Screen("account")
    object Inventory : Screen("inventory")
    object EditItem : Screen("edit_item/{itemId}") {
        fun createRoute(itemId: String) = "edit_item/$itemId"
    }
    object Splash : Screen("splash")
    object CreateItem : Screen("create_item")
    object OfferItem : Screen("offer_item/{itemId}") {
        fun createRoute(itemId: String) = "offer_item/$itemId"
    }
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: String) = "item_detail/$itemId"
    }
    object Settings : Screen("settings")
}

@Composable
fun TruekeNavGraph(repository: TruekeRepository) {
    val navController = rememberNavController()
    val itemSharedViewModel: ItemSharedViewModel = viewModel()
    LaunchedEffect(Unit) {
        SessionManager.sessionExpired.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
    composable(Screen.Splash.route) {
        SplashScreen(
            repository = repository,
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    }
            }
        )
    }

    composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                repository = repository
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() },
                repository = repository
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSent = { navController.navigate(Screen.OffersSent.route) },
                onNavigateToReceived = { navController.navigate(Screen.OffersReceived.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                onCreateItem = { navController.navigate(Screen.CreateItem.route) },
                onItemClick = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) },
                repository = repository,
                itemSharedViewModel = itemSharedViewModel

            )

        }
        composable(Screen.OffersSent.route) {
            OffersSentScreen(
                repository = repository,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToReceived = { navController.navigate(Screen.OffersReceived.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
            )


        }
        composable(Screen.OffersReceived.route) {
            OffersReceivedScreen(
                repository = repository,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSent = { navController.navigate(Screen.OffersSent.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
            )

        }
        composable(Screen.Account.route) {
            val scope = rememberCoroutineScope()

            AccountScreen(
                onInventoryClick = { navController.navigate(Screen.Inventory.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    scope.launch {
                        repository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                repository = repository,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSent = { navController.navigate(Screen.OffersSent.route) },
                onNavigateToReceived = { navController.navigate(Screen.OffersReceived.route) }
            )
        }
        composable(
            Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            val item = itemSharedViewModel.getItem(itemId)

            if (item != null) {
                ItemDetailScreen(
                    item = item,
                    onOfferClick = { selectedItem ->
                        navController.navigate(Screen.OfferItem.createRoute(selectedItem.id))
                    },
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Muestra mensaje si el item no fue encontrado
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Item no encontrado", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        composable(
            Screen.EditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            EditItemScreen(
                itemId = itemId,
                itemSharedViewModel = itemSharedViewModel,
                repository = repository,
                onEditSuccess = { navController.popBackStack() }
            )
        }



        composable(Screen.Inventory.route) {
            InventoryScreen(repository = repository,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSent = { navController.navigate(Screen.OffersSent.route) },
                onCreateItem = { navController.navigate(Screen.CreateItem.route) },
                onEditItem = { item ->
                    itemSharedViewModel.setItem(item.toItem()) // si necesitas convertir de ItemMine a Item
                    navController.navigate(Screen.EditItem.createRoute(item.id))
                },
                onNavigateToReceived = { navController.navigate(Screen.OffersReceived.route)}

            )

        }
        composable(Screen.CreateItem.route) {
            CreateItemScreen(
                onItemCreated = { navController.popBackStack() },
                repository = repository
            )
        }
        composable(
            Screen.OfferItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            OfferItemScreen(itemId = itemId, repository = repository) { navController.popBackStack() }
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Account.route) {
            AccountScreen(
                onInventoryClick = { navController.navigate(Screen.Inventory.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                repository = repository,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToSent = { navController.navigate(Screen.OffersSent.route) },
                onNavigateToReceived = { navController.navigate(Screen.OffersReceived.route) }
            )
        }

    }
}


