package com.danpike.socialapp.api.responses

data class SignUpErrorResponse(
    var message: String? = null,
    var stack: String? = null
)