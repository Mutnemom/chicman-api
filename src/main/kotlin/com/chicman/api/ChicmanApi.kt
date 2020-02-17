package com.chicman.api

import com.chicman.api.controller.AuthenticationController
import com.chicman.api.security.jwt.JwtProvider
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.text.DateFormat

@Suppress("unused")
fun Application.main() {
    DatabaseProvider.initDatabase()

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    install(Authentication) {
        jwt {
            verifier(JwtProvider.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("username").asString())
                UserIdPrincipal(it.payload.getClaim("type").asString())
                UserIdPrincipal(it.payload.getClaim("uid").asString())
            }
        }
    }

    install(Routing) {
        authenticate {
            get("$API_V1/") { call.respondText("Hello, Mutnemom!") }
        }

        post("$API_V1/auth/login/password") { AuthenticationController(this).login() }
    }
}
