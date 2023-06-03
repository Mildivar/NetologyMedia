package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
    }

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        appAuth:AppAuth
    ):OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = appAuth.data.value?.token?.let {
                chain.request().newBuilder()
                    .addHeader("Authorization", it)
                    .build()
            } ?: chain.request()

            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .let {
            if (BuildConfig.DEBUG) {
                it.addInterceptor(logging)
            } else it
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ):Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideApiService(
        retrofit:Retrofit
    ):ApiService = retrofit.create()
    }