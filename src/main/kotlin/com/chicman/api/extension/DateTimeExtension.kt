package com.chicman.api.extension

import com.google.gson.internal.bind.util.ISO8601Utils
import org.joda.time.DateTime

fun DateTime.formatISO8601(): String = try {
    ISO8601Utils.format(this.toDate())
} catch (e: Throwable) {
    e.printStackTrace()
    this.toString()
}