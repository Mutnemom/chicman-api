package com.chicman.api.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtProvider {

    private const val SECRET = "secretnamcihc"

    private val algorithm = Algorithm.HMAC256(SECRET)
    val verifier: JWTVerifier
        get() = JWT.require(algorithm).build()

    fun createToken(uid: String, username: String, profileName: String, type: String): String =
        JWT.create()
            .withClaim("uid", uid)
            .withClaim("username", username)
            .withClaim("profile", profileName)
            .withClaim("type", type)
            .withIssuedAt(Date())
            .sign(algorithm)

}
