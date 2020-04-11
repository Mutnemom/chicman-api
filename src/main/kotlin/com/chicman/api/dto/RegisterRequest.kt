package com.chicman.api.dto

data class RegisterRequest(
    val profileName: String,
    val username: String,
    val password: String,
    val redirectUrl: String? = null
)
