package com.chicman.api.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.chicman.api.AUDIENCE
import com.chicman.api.GUEST_USER
import com.chicman.api.ISSUER
import com.chicman.api.SECRET
import java.util.*

object JwtProvider {

    private val algorithm = Algorithm.HMAC256(SECRET)
    val verifier: JWTVerifier
        get() = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .build()

    fun createToken(
        uid: String? = UUID.randomUUID().toString(),
        type: String? = GUEST_USER,
        username: String? = null,
        profileName: String? = null,
        createAt: String? = null,
        activateAt: String? = null
    ): String = JWT.create()
        .withClaim("activateAt", activateAt)
        .withClaim("createAt", createAt)
        .withClaim("username", username)
        .withClaim("profile", profileName)
        .withClaim("type", type)
        .withClaim("uid", uid)
        .withAudience(AUDIENCE)
        .withIssuedAt(Date())
        .withIssuer(ISSUER)
        .sign(algorithm)

    fun createVerificationToken(memberId: String, createAt: Date, expiresAt: Date): String =
        JWT.create()
            .withClaim("memberId", memberId)
            .withAudience(AUDIENCE)
            .withIssuedAt(createAt)
            .withExpiresAt(expiresAt)
            .withIssuer(ISSUER)
            .sign(algorithm)

}
