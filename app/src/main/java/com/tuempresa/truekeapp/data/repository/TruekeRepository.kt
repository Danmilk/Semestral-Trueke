package com.tuempresa.truekeapp.data.repository

import com.tuempresa.truekeapp.data.api.TruekeApi
import com.tuempresa.truekeapp.datastore.TokenDataStore
import com.tuempresa.truekeapp.data.model.AuthData
import com.tuempresa.truekeapp.data.model.CreateOfferRequest
import com.tuempresa.truekeapp.data.model.CreateItemResponse
import com.tuempresa.truekeapp.data.model.Item
import com.tuempresa.truekeapp.data.model.Offer
import com.tuempresa.truekeapp.data.model.LoginRequest
import com.tuempresa.truekeapp.data.model.RegisterRequest
import com.tuempresa.truekeapp.data.model.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class TruekeRepository(
    private val api: TruekeApi,
    private val tokenDataStore: TokenDataStore
) {

    /** Registro de usuario */
    suspend fun register(email: String, password: String): Response<Void> {
        val body = RegisterRequest(email = email, password = password)
        return api.register(body)
    }

    /** Login y guardado del JWT */
    suspend fun login(email: String, password: String): ApiResponse<AuthData>? {
        val body = LoginRequest(email = email, password = password)
        val response = api.login(body)
        if (response.isSuccessful) {
            val apiResponse = response.body()
            apiResponse?.data?.token?.let { token ->
                tokenDataStore.saveToken(token)
            }
            return apiResponse
        } else {
            // Podrías mapear errores más finamente
            throw Exception("Login failed: ${response.code()} ${response.message()}")
        }
    }

    /** Cerrar sesión (borrar token local) */
    suspend fun logout() {
        tokenDataStore.clearToken()
    }

    /** Eliminar cuenta */
    suspend fun deleteAccount(): Response<ApiResponse<Unit>> {
        // El interceptor añade el header Authorization automáticamente
        return api.deleteAccount()
    }

    /** Crear un nuevo item con imagen */
    suspend fun createItem(
        title: String,
        description: String,
        filePart: MultipartBody.Part
    ): ApiResponse<CreateItemResponse>? {
        // Prepara los campos como RequestBody si es necesario
        val response = api.createItem(
            title = RequestBody.create(MultipartBody.FORM, title),
            description = RequestBody.create(MultipartBody.FORM, description),
            file = filePart
        )
        if (response.isSuccessful) {
            return response.body()?.data as ApiResponse<CreateItemResponse>?
        } else {
            throw Exception("Create item failed: ${response.code()} ${response.message()}")
        }
    }

    /** Obtener lista de items disponibles */
    suspend fun getItems(): List<Item> {
        val response = api.getItems()
        if (response.isSuccessful) {
            return response.body()?.data?.items ?: emptyList()
        } else {
            throw Exception("Get items failed: ${response.code()} ${response.message()}")
        }
    }

    /** Obtener lista de mis items */
    suspend fun getMyItems(): List<Item> {
        val response = api.getMyItems()
        if (response.isSuccessful) {
            return (response.body()?.data?.items ?: emptyList()) as List<Item>
        } else {
            throw Exception("Get my items failed: ${response.code()} ${response.message()}")
        }
    }

    /** Eliminar un item por ID */
    suspend fun deleteItem(itemId: String): Response<ApiResponse<Unit>> {
        return api.deleteItem(itemId)
    }

    /** Crear una oferta */
    suspend fun createOffer(itemId: String, offeredItemIds: List<String>): ApiResponse<Unit>? {
        val request = CreateOfferRequest(itemId = itemId, offeredItemIds = offeredItemIds)
        val response = api.createOffer(request)
        if (response.isSuccessful) {
            return response.body()?.data as ApiResponse<Unit>?
        } else {
            throw Exception("Create offer failed: ${response.code()} ${response.message()}")
        }
    }

    /** Obtener ofertas recibidas para un item */
    suspend fun getOffers(itemId: String): List<Offer> {
        val response = api.getOffers(itemId)
        if (response.isSuccessful) {
            return response.body()?.data?.offers ?: emptyList()
        } else {
            throw Exception("Get offers failed: ${response.code()} ${response.message()}")
        }
    }
}
