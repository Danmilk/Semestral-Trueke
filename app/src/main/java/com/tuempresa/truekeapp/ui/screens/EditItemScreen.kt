package com.tuempresa.truekeapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tuempresa.truekeapp.data.model.Item
import com.tuempresa.truekeapp.data.model.ItemSharedViewModel
import com.tuempresa.truekeapp.data.repository.TruekeRepository
import com.tuempresa.truekeapp.ui.components.LoadingIndicator
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    itemId: String,
    itemSharedViewModel: ItemSharedViewModel,
    repository: TruekeRepository,
    onEditSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val item = itemSharedViewModel.getItem(itemId) ?: return

    var title by remember { mutableStateOf(TextFieldValue(item.title)) }
    var description by remember { mutableStateOf(TextFieldValue(item.description)) }
    var selectedStatus by remember { mutableStateOf(item.status) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val statusOptions = listOf("public", "private", "both")
    var expanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Item") })
        }
    ) { innerPadding ->
        if (isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown de status
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedStatus,
                        onValueChange = {},
                        label = { Text("Visibilidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedStatus = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cambiar Imagen (opcional)")
                }

                Image(
                    painter = rememberAsyncImagePainter(
                        imageUri ?: item.imageUrl
                    ),
                    contentDescription = "Imagen del item",
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val titlePart = RequestBody.create(MultipartBody.FORM, title.text)
                                val descPart = RequestBody.create(MultipartBody.FORM, description.text)
                                val statusPart = RequestBody.create(MultipartBody.FORM, selectedStatus)

                                val filePart = imageUri?.let {
                                    val file = com.tuempresa.truekeapp.util.FileUtils.getFileFromUri(context, it)
                                    val mime = context.contentResolver.getType(it)
                                    val requestFile = file.asRequestBody(mime?.toMediaTypeOrNull())
                                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                                }

                                val response = repository.editItem(
                                    itemId = item.id,
                                    title = titlePart.toString(),
                                    description = descPart.toString(),
                                    status = statusPart.toString(),
                                    filePart = filePart
                                )

                                if (response.isSuccessful) {
                                    onEditSuccess()
                                } else {
                                    throw Exception("Error ${response.code()}: ${response.message()}")
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
