package com.chicman.api.extension

import com.auth0.jwt.interfaces.Claim
import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal

@Throws(IllegalStateException::class)
fun ApplicationCall.getUidClaim(): Claim =
    authentication
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("uid")
        ?: throw IllegalStateException("JWT Principal not found")
