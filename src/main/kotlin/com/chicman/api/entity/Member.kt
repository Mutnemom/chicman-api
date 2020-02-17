package com.chicman.api.entity

import org.jetbrains.exposed.sql.Table

object Member : Table("member") {
    val uid = varchar("uid", length = 36)
    val username = varchar("username", length = 45)
    val password = varchar("password", length = 45)
    val profileName = varchar("profile_name", length = 45)
    val type = varchar("type", length = 6)
}
