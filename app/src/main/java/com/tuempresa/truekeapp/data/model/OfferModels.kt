package com.tuempresa.truekeapp.data.model

data class CreateOfferRequest(
    val itemId: String,
    val offeredItemIds: List<String>
)

data class OfferedItem(
    val id: String,
    val title: String,
    val imageUrl: String
)

data class Offer(
    val id: String,
    val offeredBy: User,
    val offeredItems: List<OfferedItem>
)

data class OffersResponse(
    val offers: List<Offer>
)
