package com.chicman.api.dto

data class MemberProfilesResponse(
    val uid: String,
    val type: String,
    val username: String,
    val profileName: String
)
