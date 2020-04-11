package com.chicman.api.service

import com.chicman.api.dto.LoginPasswordResponse
import com.chicman.api.dto.MembersActivateResponse
import com.chicman.api.dto.MembersProfilesResponse
import com.chicman.api.entity.Member
import com.chicman.api.extension.formatISO8601
import com.chicman.api.security.jwt.JwtProvider
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTimeZone
import java.util.*

object MemberService {

    fun activateUser(verifier: String): MembersActivateResponse? = try {
        MembersActivateResponse(verifier)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

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
                            ).also { token -> response.add(LoginPasswordResponse(token)) }
                        }
                    }
            }
        }

        response
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    fun getProfiles(memberId: String): MembersProfilesResponse? = try {
        transaction {
            with(Member) {
                select { (uid eq memberId) }
                    .firstOrNull()
                    ?.let {
                        MembersProfilesResponse(
                            it[uid],
                            it[type],
                            it[username],
                            it[createAt].formatISO8601(),
                            it[activateAt].formatISO8601(),
                            it[profileName]
                        )
                    }
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    fun createMember(
        email: String,
        pwd: String,
        alias: String
    ): MembersProfilesResponse? = try {
        val memberId = UUID.randomUUID().toString()
        transaction {
            Member.insert {
                it[uid] = memberId
                it[username] = email
                it[password] = pwd
                it[profileName] = alias
            }
            with(Member) {
                select { (uid eq memberId) }
                    .firstOrNull()
                    ?.let {
                        MembersProfilesResponse(
                            it[uid],
                            it[type],
                            it[username],
                            it[createAt].formatISO8601(),
                            it[activateAt].formatISO8601(),
                            it[profileName]
                        )
                    } ?: run { null }
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    fun isExistingUsername(newUsername: String): Boolean = try {
        transaction {
            with(Member) {
                select { (username eq newUsername) }
                    .firstOrNull() != null
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }

}
