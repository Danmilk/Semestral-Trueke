package com.tuempresa.truekeapp.data.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val ownerId: String,
    val isMine: Boolean? = null,          // Solo está presente en /api/items
    val owner: User? = null               // Solo está presente en /api/items
)


data class CreateItemResponse(
    val item: Item
)

data class ItemsListResponse(
    val items: List<Item>
)

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
