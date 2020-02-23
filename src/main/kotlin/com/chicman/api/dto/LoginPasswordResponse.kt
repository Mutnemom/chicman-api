package com.chicman.api.dto

data class LoginPasswordResponse(
    val uid: String,
    val type: String,
    val username: String,
    val profileName: String,
    val accessToken: String
)
