package com.chicman.api

import com.chicman.api.controller.AuthenticationController
import com.chicman.api.controller.MemberController
import com.chicman.api.security.jwt.JwtProvider
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
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
                when {
                    !it.payload.audience.contains(AUDIENCE) -> null
                    else -> JWTPrincipal(it.payload)
                }
            }
        }
    }

    install(Routing) {
        get("$API_V1/auth/guest") { AuthenticationController(this).createGuestToken() }

        authenticate {
            post("$API_V1/auth/login/password") { AuthenticationController(this).login() }
            post("$API_V1/auth/register") { AuthenticationController(this).register() }

            get("$API_V1/members/profiles") { MemberController(this).getProfiles() }
            put("$API_V1/members/{id}/activate") { MemberController(this).activate() }
        }

    }

}
