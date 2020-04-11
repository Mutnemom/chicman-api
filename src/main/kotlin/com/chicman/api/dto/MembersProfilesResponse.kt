package com.chicman.api.dto

data class MembersProfilesResponse(
    val uid: String,
    val type: String,
    val username: String,
    val createAt: String,
    val activateAt: String,
    val profileName: String
)
