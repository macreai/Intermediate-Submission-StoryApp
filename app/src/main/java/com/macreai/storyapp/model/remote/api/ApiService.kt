package com.macreai.storyapp.model.remote.api

import com.macreai.storyapp.model.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PublicResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") user: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ListStoryResponse

    @Multipart
    @POST("stories")
    fun postPicture(
        @Header("Authorization") user: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<SendStoryResponse>

    @GET("stories")
     fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1
    ): Call<ListStoryResponse>

}