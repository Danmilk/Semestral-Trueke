package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.tuempresa.truekeapp.data.model.Item
import com.tuempresa.truekeapp.data.model.ItemSharedViewModel
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.components.ItemCard
import com.tuempresa.truekeapp.ui.components.LoadingIndicator
import com.tuempresa.truekeapp.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    itemSharedViewModel: ItemSharedViewModel,
    onNavigateToSent: () -> Unit,
    onNavigateToReceived: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onItemClick: (String) -> Unit,
    onCreateItem: () -> Unit,
    repository: TruekeRepository
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false)
    }

    // Carga de items
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            items = repository.getItems()
        } catch (e: Exception) {
            // Manejo de error opcional
        }
        isLoading = false
    }

    val filteredItems = items.filter {
        it.isMine != true &&
                (it.status == "public" || it.status == "both") &&
                it.title.contains(searchQuery.text, ignoreCase = true)
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trueke - Items") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateItem) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo item")
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Home.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.OffersSent.route -> onNavigateToSent()
                        Screen.OffersReceived.route -> onNavigateToReceived()
                        Screen.Account.route -> onNavigateToAccount()
                        // Ya estás en Home
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar items...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                LoadingIndicator()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = {
                                itemSharedViewModel.setItem(item)  // Guardas el item
                                onItemClick(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
