package com.danpike.socialapp

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @FormUrlEncoded
    @POST("user/signup")
    fun signUp(
        @Field("firstName") firstName: String?,
        @Field("lastName") lastName: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<User>
}