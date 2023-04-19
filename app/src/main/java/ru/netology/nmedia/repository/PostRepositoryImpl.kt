package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.HttpException
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import kotlin.Exception


class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAllAsync() {
        val postsResponse = PostsApi.retrofitService.getAll()
        if (!postsResponse.isSuccessful) {
            throw HttpException(postsResponse)
        }
        val posts = postsResponse.body().orEmpty()
        postDao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun likeById(id: Long): Post {
//        try {
        val like = PostsApi.retrofitService.likeById(id)
        if (!like.isSuccessful) {
            throw HttpException(like)
        }
        val likes = like.body() ?: throw NullPointerException()
        postDao.likeById(id)
//
//        } catch (e:Exception){
//
//        }
        return likes
    }

    override suspend fun unlikeById(id: Long): Post {
//        try {
        val like = PostsApi.retrofitService.likeById(id)
        if (!like.isSuccessful) {
            throw HttpException(like)
        }
        val likes = like.body() ?: throw NullPointerException()
        postDao.likeById(id)
//
//        } catch (e:Exception){
//
//        }
        return likes
    }

//override fun unlikeById(id: Long, callback: PostRepository.GetAllCallback<Post>) {
//
//    PostsApi.retrofitService.unlikeById(id)
//        .enqueue(object : retrofit2.Callback<Post> {
//            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
//                callback.onError(RuntimeException(t))
//            }
//
//            override fun onResponse(
//                call: retrofit2.Call<Post>,
//                response: retrofit2.Response<Post>
//            ) {
//                if (!response.isSuccessful) {
//                    callback.onError(Exception(response.message()))
//                    return
//                }
//                val posts = response.body()
//                if (posts == null) {
//                    callback.onError(RuntimeException("Body is null"))
//                    return
//                }
//                callback.onSuccess(posts)
//            }
//        }
//        )
//}

    override suspend fun save(post: Post) {
        try {
            val save = PostsApi.retrofitService.save(post)
            if (!save.isSuccessful) {
                throw HttpException(save)
            }
            val body = save.body() ?: throw RuntimeException()
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }


override suspend fun removeById(id: Long) {
    val remove = PostsApi.retrofitService.deleteById(id)
    if (!remove.isSuccessful) {
        throw HttpException(remove)
    }
    val post = remove.body() ?: throw NullPointerException()
    postDao.removeById(id)
}

}


