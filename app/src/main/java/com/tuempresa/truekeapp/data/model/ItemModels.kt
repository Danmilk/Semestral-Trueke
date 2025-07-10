package com.tuempresa.truekeapp.data.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val ownerId: String
)

data class CreateItemResponse(
    val item: Item
)

data class ItemsListResponse(
    val items: List<Item>
)

// Extiende Item con campos extra si los necesitas
data class ItemMine(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val ownerId: String,
    val owner: User,
    val isMine: Boolean
)

data class ItemsMineResponse(
    val items: List<ItemMine>
)
