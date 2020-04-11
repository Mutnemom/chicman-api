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
const val MESSAGE_USERNAME_NOT_AVAILABLE = "Username is not available"
const val MESSAGE_FAILED_TO_INSERT_MEMBER = "Failed to insert new member"

/* for http redirect message */
const val REDIRECT_PREFIX = "{\"redirectUrl\": \""
const val REDIRECT_POSTFIX = ERROR_POSTFIX
