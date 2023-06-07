//package ru.netology.nmedia.di
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.api.ApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//import java.util.concurrent.TimeUnit
//
//class DependencyContainer(
//    private val context: Context
//) {
//    companion object {
//        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context){
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//            }
//        }
//
//    private val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    val appAuth = AppAuth(context)
//
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .addInterceptor { chain ->
//            val request = appAuth.data.value?.token?.let {
//                chain.request().newBuilder()
//                    .addHeader("Authorization", it)
//                    .build()
//            } ?: chain.request()
//
//            chain.proceed(request)
//        }
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .let {
//            if (BuildConfig.DEBUG) {
//                it.addInterceptor(logging)
//            } else it
//        }
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .baseUrl(BASE_URL)
//        .build()
//
//
//    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
//        .fallbackToDestructiveMigration()
//        .build()
//
//     val apiService = retrofit.create<ApiService>()
//
//    private val postDao = appDb.postDao()
//
//    val repository: PostRepository = PostRepositoryImpl(
//        postDao,
//        apiService
//    )
//}