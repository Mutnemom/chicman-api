package com.chicman.api.extension

import com.auth0.jwt.interfaces.Claim
import com.chicman.api.ERROR_POSTFIX
import com.chicman.api.ERROR_PREFIX
import com.chicman.api.REDIRECT_POSTFIX
import com.chicman.api.REDIRECT_PREFIX
import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

@Throws(IllegalStateException::class)
fun ApplicationCall.getUidClaim(): Claim =
    authentication
        .principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("uid")
        ?: throw IllegalStateException("JWT Principal not found")

suspend fun ApplicationCall.respondErrorJson(errorMsg: String, httpStatus: HttpStatusCode): Unit =
    this.respondText(
        "$ERROR_PREFIX$errorMsg$ERROR_POSTFIX",
        ContentType.Application.Json,
        httpStatus
    )

suspend fun ApplicationCall.respondRedirect(redirectUrl: String): Unit =
    this.respondText(
        "$REDIRECT_PREFIX$redirectUrl$REDIRECT_POSTFIX",
        ContentType.Application.Json,
        HttpStatusCode.OK
    )
