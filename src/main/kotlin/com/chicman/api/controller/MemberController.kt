package com.chicman.api.controller

import com.chicman.api.ERROR_POSTFIX
import com.chicman.api.ERROR_PREFIX
import com.chicman.api.MESSAGE_UNAUTHORIZED
import com.chicman.api.dto.LoginPasswordRequest
import com.chicman.api.extension.errorAware
import com.chicman.api.extension.getUidClaim
import com.chicman.api.extension.valueString
import com.chicman.api.service.MemberService
import com.chicman.api.utils.LogUtils
import com.google.gson.JsonParseException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext

class MemberController(private val context: PipelineContext<Unit, ApplicationCall>) {

    suspend fun verify() {
        LogUtils.info(context.call.request.path())
        context.errorAware {
            context.call.receive<LoginPasswordRequest>()
                .apply {
                    val resp = MemberService.getUsers(username, password)
                    when {
                        resp.isNullOrEmpty() -> context.call.respondText(
                            "$ERROR_PREFIX$MESSAGE_UNAUTHORIZED$ERROR_POSTFIX",
                            ContentType.Application.Json,
                            HttpStatusCode.Unauthorized
                        )
                        else -> context.call.respond(resp)
                    }
                }
        }
        LogUtils.info("${context.call.response.status()?.value}")
    }

    suspend fun getProfiles() = context.apply {
        LogUtils.info(call.request.path())
        errorAware {
            call.apply {
                val claim = getUidClaim()
                val memberId = claim.valueString ?: throw JsonParseException("extract claim uid")
                respond(MemberService.getProfiles(memberId)!!)
            }
        }
        LogUtils.info("${call.response.status()?.value}")
    }

}
