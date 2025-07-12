package com.tuempresa.truekeapp.ui.navigation

import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object OffersSent : Screen("offers_sent")
    object OffersReceived : Screen("offers_received")
    object Account : Screen("account")
    object Inventory : Screen("inventory")
    object Splash : Screen("splash")
    object CreateItem : Screen("create_item")
    object OfferItem : Screen("offer_item/{itemId}") {
        fun createRoute(itemId: String) = "offer_item/$itemId"
    }
    object Settings : Screen("settings")
}

@Composable
fun TruekeNavGraph(repository: TruekeRepository) {
    val navController = rememberNavController()

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
                repository = repository
            )
        }
        composable(Screen.OffersSent.route) {
            OffersSentScreen(repository = repository)
        }
        composable(Screen.OffersReceived.route) {
            OffersReceivedScreen(repository = repository)
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
                repository = repository
            )
        }
        composable(Screen.Inventory.route) {
            InventoryScreen(repository = repository)
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
                repository = repository
            )
        }

    }
}


