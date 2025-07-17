package com.tuempresa.truekeapp.data.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val ownerId: String,
    val isMine: Boolean? = null,
    val owner: User? = null
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
fun ItemMine.toItem(): Item {
    return Item(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        status = status,
        ownerId = ownerId,
        isMine = isMine,
        owner = owner
    )
}
