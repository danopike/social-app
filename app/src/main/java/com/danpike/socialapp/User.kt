package com.danpike.socialapp

data class User(
    var token: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var id: String? = null,
    var email: String? = null,
    var friends: List<String>? = null
)
