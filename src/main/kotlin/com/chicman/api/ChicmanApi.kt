package com.chicman.api

import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList

@Suppress("unused")
fun Application.main() {
    initDatabase()
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") { call.respondText("Hello, Mutnemom!") }
        get("/token") { call.respondText(createSampleJwt()) }
        get("/api/products") { call.respondText(getCustomers(), ContentType.Application.Json) }
    }
}

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

fun createSampleJwt(): String = Jwts.builder()
    .setClaims(hashMapOf("id" to "111") as Map<String, Any>?)
    .setHeaderParam("typ", "JWT")
    .setSubject("ChicmanToken")
    .setIssuedAt(Date(System.currentTimeMillis()))
    .signWith(SignatureAlgorithm.HS256, "secret")
    .compact()

fun getCustomers(): String = try {
    transaction {
        val response = Customer.selectAll().orderBy(Customer.customerId, false)
        val dataModel = ArrayList<CustomerModel>()
        for (it in response) {
            dataModel.add(
                CustomerModel(
                    it[Customer.customerId],
                    it[Customer.fullName],
                    it[Customer.email],
                    it[Customer.password],
                    it[Customer.type],
                    it[Customer.enabled]
                )
            )
        }

        val map = mutableMapOf<String, ArrayList<CustomerModel>>()
        map["customers"] = dataModel
        Gson().toJson(map)
    }
} catch (e: Throwable) {
    e.printStackTrace()
    "{}"
}

object Customer : Table("customer") {
    val customerId = integer("customer_id")
    val fullName = varchar("full_name", length = 45)
    val email = varchar("email", length = 45)
    val password = varchar("password", length = 45)
    val type = varchar("type", length = 1)
    val enabled = bool("enabled")

}

data class CustomerModel(
    var customerId: Int?,
    var fullName: String?,
    var email: String?,
    var password: String?,
    var type: String?,
    var enabled: Boolean?
)
