package com.chicman.api.extension

import com.auth0.jwt.interfaces.Claim
import com.google.gson.Gson
import com.google.gson.JsonObject

val Claim.valueString: String?
    get() = try {
        Gson().let {
            it.fromJson(it.toJson(this), JsonObject::class.java)["data"]
                .asJsonObject["_value"]
                .asString
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
