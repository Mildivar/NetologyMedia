package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .let {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(logging)
        } else it
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface PostApiService {

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

//    @DELETE("posts/{id}/likes")
//    suspend fun unlikeById(@Path("id") postId: Long): Response<Post>
}

object PostsApi {
    val retrofitService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}