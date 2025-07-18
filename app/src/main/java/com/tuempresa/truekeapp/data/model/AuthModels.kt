package com.tuempresa.truekeapp.data.model

data class RegisterRequest(
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class User(
    val id: String,
    val email: String,
    val createdAt: String
)

data class AuthData(
    val token: String,
    val user: User
)

data class ApiResponse<T>(
    val error: Boolean,
    val message: String,
    val data: T?
)
