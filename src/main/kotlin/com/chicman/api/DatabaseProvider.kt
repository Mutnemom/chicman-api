package com.chicman.api

import io.ktor.application.Application
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.util.*

object DatabaseProvider {

    fun initDatabase() {
        try {
            val ips = Application::class.java.classLoader.getResourceAsStream("database.properties")
            val props = Properties()
            props.load(ips)

            Database.connect(
                url = props["database.url"] as String,
                driver = props["database.driver"] as String,
                user = props["database.username"] as String,
                password = props["database.password"] as String
            )

            LoggerFactory.getLogger(Application::class.java.simpleName)
                .apply { info("initialized database") }

        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}
