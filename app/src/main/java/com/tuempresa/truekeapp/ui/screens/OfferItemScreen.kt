package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.truekeapp.data.model.Item
import com.tuempresa.truekeapp.data.model.ItemMine
import com.tuempresa.truekeapp.data.model.ItemSharedViewModel
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferItemScreen(
    itemId: String,
    repository: TruekeRepository,
    onOfferSent: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var myItems by remember { mutableStateOf<List<ItemMine>>(emptyList()) }
    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var targetItem by remember { mutableStateOf<Item?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val allItems = repository.getItems()
            targetItem = allItems.find { it.id == itemId }
            myItems = repository.getMyItems()
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ofertar por item") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                targetItem?.let { item ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Item objetivo:", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Card {
                            Row(modifier = Modifier.padding(12.dp)) {
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
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Selecciona uno de tus items para ofertar:")
                    }

                    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                        items(myItems) { myItem ->
                            val isSelected = myItem.id == selectedItemId
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItemId = if (isSelected) null else myItem.id
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(myItem.imageUrl),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(myItem.title, style = MaterialTheme.typography.titleMedium)
                                        Text(myItem.description, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                if (selectedItemId != null) {
                                    try {
                                        repository.createOffer(itemId, listOf(selectedItemId!!))
                                        onOfferSent()
                                    } catch (e: Exception) {
                                        errorMessage = e.message
                                    }
                                }
                            }
                        },
                        enabled = selectedItemId != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar oferta")
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } ?: run {
                    Text(
                        text = "No se encontró el item objetivo.",
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }
}
