// File: app/src/main/java/com/truekeapp/data/api/TruekeApi.kt
package com.tuempresa.truekeapp.data.api

import com.tuempresa.truekeapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface TruekeApi {

    // --- Autenticación ---
    @POST("/api/auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<Void>

    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<AuthData>>

    @DELETE("/api/auth/delete")
    suspend fun deleteAccount(): Response<ApiResponse<Unit>>

    // --- Items ---
    @Multipart
    @POST("/api/items")
    suspend fun createItem(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<CreateItemResponse>>

    @GET("/api/items")
    suspend fun getItems(): Response<ApiResponse<ItemsListResponse>>

    @GET("/api/items/mine")
    suspend fun getMyItems(): Response<ApiResponse<ItemsMineResponse>>

    @DELETE("/api/items/{id}")
    suspend fun deleteItem(
        @Path("id") itemId: String
    ): Response<ApiResponse<Unit>>

    // --- Ofertas ---
    @POST("/api/offers")
    suspend fun createOffer(
        @Body body: CreateOfferRequest
    ): Response<ApiResponse<Unit>>

    @GET("/api/offers/{itemId}")
    suspend fun getOffers(
        @Path("itemId") itemId: String
    ): Response<ApiResponse<OffersResponse>>
}
