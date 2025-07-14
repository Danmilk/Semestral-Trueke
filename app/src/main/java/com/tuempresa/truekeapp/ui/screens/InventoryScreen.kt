package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.truekeapp.data.model.ItemMine
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.components.LoadingIndicator
import com.tuempresa.truekeapp.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    repository: TruekeRepository,
    onCreateItem: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSent: () -> Unit,
    onNavigateToReceived: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<ItemMine>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loadMyItems(repository) { result ->
            isLoading = false
            result.onSuccess { items = it }
            result.onFailure { errorMessage = it.message }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Inventario") })
        },

        floatingActionButton = {
            FloatingActionButton(onClick = onCreateItem) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo item")
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.Inventory.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> onNavigateToHome()
                        Screen.OffersSent.route -> onNavigateToSent()
                        Screen.OffersReceived.route -> onNavigateToReceived()
                        // Ya estás en Inventario
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                if (items.isEmpty()) {
                    Text(
                        text = "No tienes items aún.",
                        modifier = Modifier
                            .padding(24.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items) { item ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(modifier = Modifier.weight(1f)) {
                                        Image(
                                            painter = rememberAsyncImagePainter(item.imageUrl),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .padding(end = 12.dp)
                                        )
                                        Column {
                                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                                            Text(item.description, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    IconButton(onClick = {
                                        scope.launch {
                                            try {
                                                repository.deleteItem(item.id)
                                                items = items.filterNot { it.id == item.id }
                                            } catch (e: Exception) {
                                                errorMessage = e.message
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private suspend fun loadMyItems(
    repository: TruekeRepository,
    onResult: (Result<List<ItemMine>>) -> Unit
) {
    runCatching {
        repository.getMyItems()
    }.onSuccess {
        onResult(Result.success(it))
    }.onFailure {
        onResult(Result.failure(it))
    }
}
