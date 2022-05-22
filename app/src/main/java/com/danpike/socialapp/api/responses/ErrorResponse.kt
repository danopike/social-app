package com.danpike.socialapp.api.responses

data class ErrorResponse(
    var message: String? = null,
    var stack: String? = null
)