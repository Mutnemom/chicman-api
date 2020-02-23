package com.chicman.api.extension

import com.chicman.api.ERROR_POSTFIX
import com.chicman.api.ERROR_PREFIX
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext

suspend fun <R> PipelineContext<Unit, ApplicationCall>.errorAware(block: suspend () -> R): R? = try {
    block()
} catch (e: Throwable) {
    call.respondText(
        "$ERROR_PREFIX${e.message}$ERROR_POSTFIX",
        ContentType.Application.Json,
        HttpStatusCode.InternalServerError
    )
    null
}
