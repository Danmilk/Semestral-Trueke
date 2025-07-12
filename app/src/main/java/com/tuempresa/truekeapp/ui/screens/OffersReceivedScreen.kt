package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.truekeapp.data.model.Item
import com.tuempresa.truekeapp.data.model.Offer
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.components.LoadingIndicator
import com.tuempresa.truekeapp.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersReceivedScreen(repository: TruekeRepository) {
    val scope = rememberCoroutineScope()
    var myItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var offersMap by remember { mutableStateOf<Map<String, List<Offer>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            myItems = repository.getMyItems()
            val map = mutableMapOf<String, List<Offer>>()
            for (item in myItems) {
                val offers = repository.getOffers(item.id)
                if (offers.isNotEmpty()) {
                    map[item.id] = offers
                }
            }
            offersMap = map
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ofertas Recibidas") })
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.OffersReceived.route,
                onNavigate = { /* Puedes conectar navegación aquí si gustas */ }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                LoadingIndicator()
            } else if (offersMap.isEmpty()) {
                Text(
                    text = "No has recibido ofertas aún.",
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    offersMap.forEach { (itemId, offers) ->
                        val item = myItems.find { it.id == itemId }
                        item?.let {
                            item {
                                Text(
                                    text = "Ofertas para: ${item.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            offers.forEach { offer ->
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Ofrecido por: ${offer.offeredBy.email}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            offer.offeredItems.forEach { offeredItem ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                ) {
                                                    Image(
                                                        painter = rememberAsyncImagePainter(offeredItem.imageUrl),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .padding(end = 12.dp)
                                                    )
                                                    Text(
                                                        text = offeredItem.title,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
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
