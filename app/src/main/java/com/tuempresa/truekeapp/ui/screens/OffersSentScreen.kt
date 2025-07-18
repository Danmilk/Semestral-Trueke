package com.tuempresa.truekeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.data.model.Offer
import com.tuempresa.truekeapp.ui.components.BottomNavBar
import com.tuempresa.truekeapp.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersSentScreen(
    repository: TruekeRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToReceived: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            offers = repository.getMyOffers()
        } catch (e: Exception) {
            errorMessage = "Error al cargar ofertas: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ofertas Enviadas") })
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = Screen.OffersSent.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> onNavigateToHome()
                        Screen.OffersReceived.route -> onNavigateToReceived()
                        Screen.Account.route -> onNavigateToAccount()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            when {
                isLoading -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando ofertas...")
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                offers.isEmpty() -> {
                    Text(
                        text = "No has enviado ofertas aún.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(offers) { offer ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Oferta enviada a:",
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    // Item objetivo
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = rememberAsyncImagePainter(offer.item.imageUrl),
                                            contentDescription = null,
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(offer.item.title, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Items ofrecidos:")
                                    offer.offeredItems.forEach { offered ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(offered.imageUrl),
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(offered.title)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Botón de eliminar
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    repository.deleteOffer(offer.id)
                                                    offers = repository.getMyOffers()
                                                } catch (e: Exception) {
                                                    errorMessage = "Error al eliminar oferta: ${e.message}"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Eliminar oferta")
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
