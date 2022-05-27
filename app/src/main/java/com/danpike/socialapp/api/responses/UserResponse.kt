package com.danpike.socialapp.api.responses

data class UserResponse(
    var token: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var id: String? = null,
    var email: String? = null,
    var friends: List<UserResponse>? = null
)
