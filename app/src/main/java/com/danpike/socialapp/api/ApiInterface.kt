package com.danpike.socialapp.api

import com.danpike.socialapp.api.responses.User
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

    @FormUrlEncoded
    @POST("user/signin")
    fun signIn(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<User>
}