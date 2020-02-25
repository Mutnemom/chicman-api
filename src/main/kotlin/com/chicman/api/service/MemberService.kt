package com.chicman.api.service

import com.chicman.api.dto.LoginPasswordResponse
import com.chicman.api.dto.MemberProfilesResponse
import com.chicman.api.entity.Member
import com.chicman.api.security.jwt.JwtProvider
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTimeZone

object MemberService {

    fun getUsers(username: String, password: String): MutableList<LoginPasswordResponse>? = try {
        val response = mutableListOf<LoginPasswordResponse>()
        transaction {
            with(Member) {
                select { (this@with.username eq username) and (this@with.password eq password) }
                    .forEach {
                        if (it[activateAt] > it[createAt]) {
                            JwtProvider.createToken(
                                it[uid],
                                it[type],
                                it[this.username],
                                it[profileName],
                                it[createAt].toDateTime(DateTimeZone.UTC).toString(),
                                it[activateAt].toDateTime(DateTimeZone.UTC).toString()
                            ).also { token -> response.add(LoginPasswordResponse(it[uid], token)) }
                        }
                    }
            }
        }

        response
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    fun getProfiles(memberId: String): MemberProfilesResponse? = try {
        transaction {
            with(Member) {
                select { (uid eq memberId) }
                    .firstOrNull()
                    ?.let {
                        MemberProfilesResponse(
                            it[uid],
                            it[type],
                            it[this@with.username],
                            it[profileName]
                        )
                    }
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

}
