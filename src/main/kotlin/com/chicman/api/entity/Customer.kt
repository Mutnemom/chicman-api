package com.chicman.api.entity

import org.jetbrains.exposed.sql.Table

object Customer : Table("customer") {
    val customerId = integer("customer_id")
    val fullName = varchar("full_name", length = 45)
    val email = varchar("email", length = 45)
    val password = varchar("password", length = 45)
    val type = varchar("type", length = 1)
    val enabled = bool("enabled")
}
