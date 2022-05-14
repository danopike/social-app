package com.danpike.socialapp

import com.google.gson.annotations.SerializedName

data class SignUpErrorResponse(
    var message: String? = null,
    var stack: String? = null
)