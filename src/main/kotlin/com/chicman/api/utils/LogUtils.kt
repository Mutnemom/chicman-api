package com.chicman.api.utils

import io.ktor.application.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LogUtils {

    private val logger: Logger by lazy { LoggerFactory.getLogger(Application::class.java.simpleName) }

    fun info(message: String) {
        logger.info(message)
    }

}
