package com.chicman.api

const val API_V1 = "api/v1"


/* for jwt */
const val SECRET = "secretnamcihc"
const val ISSUER = "com.chicman.api"
const val AUDIENCE = API_V1
const val GUEST_USER = "guest"


/* for http error message */
const val ERROR_PREFIX = "{\"error\": \""
const val ERROR_POSTFIX = "\"}"
const val MESSAGE_UNAUTHORIZED = "Invalid username/password supplied"
