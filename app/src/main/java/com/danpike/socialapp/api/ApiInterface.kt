package com.danpike.socialapp.api

import com.danpike.socialapp.api.responses.UserResponse
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
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/signin")
    fun signIn(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<UserResponse>

    @FormUrlEncoded
    @PUT("user/addfriend")
    fun addFriend(
        @Header("authorization") authorization: String,
        @Field("email") email: String
    ): Call<UserResponse>
}