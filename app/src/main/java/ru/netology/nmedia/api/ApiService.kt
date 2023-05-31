package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel


interface ApiService {

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part part: MultipartBody.Part): Response<Media>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path ("id")postId: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deleteById(@Path("id") postId: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") postId: Long): Response<Post>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): Response<AuthModel>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}
