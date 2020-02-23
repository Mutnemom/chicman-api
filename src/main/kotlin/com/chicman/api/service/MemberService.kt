package com.chicman.api.service

import com.chicman.api.dto.LoginPasswordResponse
import com.chicman.api.entity.Member
import com.chicman.api.security.jwt.JwtProvider
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object MemberService {

    fun getUsers(username: String, password: String): MutableList<LoginPasswordResponse>? = try {
        val response = mutableListOf<LoginPasswordResponse>()
        transaction {
            with(Member) {
                select { (this@with.username eq username) and (this@with.password eq password) }
                    .forEach {
                        JwtProvider
                            .createToken(it[uid], it[type], it[this.username], it[profileName])
                            .apply {
                                response.add(
                                    LoginPasswordResponse(
                                        it[uid],
                                        it[type],
                                        it[this@with.username],
                                        it[profileName],
                                        this
                                    )
                                )
                            }
                    }
            }
        }

        response
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

}
