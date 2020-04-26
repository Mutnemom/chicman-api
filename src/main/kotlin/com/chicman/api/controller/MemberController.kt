package com.chicman.api.controller

import com.chicman.api.dto.MembersActivateRequest
import com.chicman.api.extension.errorAware
import com.chicman.api.extension.getUidClaim
import com.chicman.api.extension.respondErrorJson
import com.chicman.api.extension.valueString
import com.chicman.api.service.MemberService
import com.chicman.api.utils.LogUtils
import com.google.gson.JsonParseException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

class MemberController(private val context: PipelineContext<Unit, ApplicationCall>) {

    suspend fun activate() {
        LogUtils.info(context.call.request.path())
        context.errorAware {
            try {
                context.call.receive<MembersActivateRequest>().apply {

                    val memberId = context.call.parameters["id"]
                        ?: throw IllegalArgumentException("Parameter id not found")

                    MemberService.activateAccount(memberId, isAdmin).let { resp ->
                        when (resp) {
                            null -> context.call.respondErrorJson(
                                "Failed to activate user",
                                HttpStatusCode.Forbidden
                            )
                            else -> context.call.respond(resp)
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()

                val errorMessage = when (e) {
                    is IllegalArgumentException -> {
                        if (e.message != null && e.message!!.startsWith("Parameter id")) {
                            e.message!!
                        } else {
                            "Required argument not found"
                        }
                    }
                    else -> e.message ?: "-"
                }

                context.call.respondErrorJson(errorMessage, HttpStatusCode.BadRequest)
            }
            LogUtils.info("${context.call.response.status()?.value}")
        }
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
