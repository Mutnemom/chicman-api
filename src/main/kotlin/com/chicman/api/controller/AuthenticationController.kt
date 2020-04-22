package com.chicman.api.controller

import com.chicman.api.MESSAGE_FAILED_TO_INSERT_MEMBER
import com.chicman.api.MESSAGE_UNAUTHORIZED
import com.chicman.api.MESSAGE_USERNAME_NOT_AVAILABLE
import com.chicman.api.dto.LoginPasswordRequest
import com.chicman.api.dto.MembersProfilesResponse
import com.chicman.api.dto.RegisterRequest
import com.chicman.api.extension.errorAware
import com.chicman.api.extension.respondErrorJson
import com.chicman.api.extension.respondRedirect
import com.chicman.api.security.jwt.JwtProvider
import com.chicman.api.service.MemberService
import com.chicman.api.utils.LogUtils
import com.chicman.api.utils.MailUtils
import com.google.gson.Gson
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthenticationController(private val context: PipelineContext<Unit, ApplicationCall>) {

    private val responseTokenKey = "accessToken"

    suspend fun createGuestToken() {
        context.call.apply {
            val map = mapOf(responseTokenKey to JwtProvider.createToken())
            respondText(
                Gson().toJson(map),
                ContentType.Application.Json
            )
        }
    }

    suspend fun login() {
        LogUtils.info(context.call.request.path())
        context.errorAware {
            context.call.receive<LoginPasswordRequest>()
                .apply {
                    val resp = MemberService.getUsers(username, password)
                    when {
                        resp.isNullOrEmpty() -> context.call
                            .respondErrorJson(MESSAGE_UNAUTHORIZED, HttpStatusCode.Unauthorized)
                        else -> context.call.respond(resp)
                    }
                }
        }
        LogUtils.info("${context.call.response.status()?.value}")
    }

    suspend fun register() {
        LogUtils.info(context.call.request.path())
        context.errorAware {
            context.call.receive<RegisterRequest>()
                .apply {
                    when {
                        MemberService.isExistingUsername(username) -> onUnavailableUsername()
                        else -> createNewAccount(username, password, profileName, redirectUrl)
                    }
                }
        }
        LogUtils.info("${context.call.response.status()?.value}")
    }

    private suspend fun createNewAccount(usr: String, pwd: String, alias: String, url: String?) {
        when (val member = MemberService.createMember(usr, pwd, alias)) {
            null -> onCreateMemberFailed()
            else -> onCreateMemberSucceed(url, member)
        }
    }

    private suspend fun onCreateMemberFailed() {
        context.call.respondErrorJson(
            MESSAGE_FAILED_TO_INSERT_MEMBER,
            HttpStatusCode.InternalServerError
        )
    }

    private suspend fun onCreateMemberSucceed(url: String?, member: MembersProfilesResponse) {
        context.call.respondRedirect(url ?: "")
        url?.also {
            withContext(Dispatchers.IO) {
                MailUtils.sendAccountActivationMail(it, member.uid, member.createAt)
            }
        }
    }

    private suspend fun onUnavailableUsername() {
        context.call.respondErrorJson(
            MESSAGE_USERNAME_NOT_AVAILABLE,
            HttpStatusCode.UnprocessableEntity
        )
    }

}
