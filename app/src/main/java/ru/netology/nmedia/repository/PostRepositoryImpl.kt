package ru.netology.nmedia.repository


import android.accounts.NetworkErrorException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentTypes
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.MediaModel
import java.io.IOException


class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAllVisible().map {
        it.map(PostEntity::toDto)
    }
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(latestId: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            try {
                val postsResponse = PostsApi.retrofitService.getNewer(latestId)
                if (!postsResponse.isSuccessful) {
                    throw ApiError(postsResponse.code(), postsResponse.message())
                }
                val posts = postsResponse.body().orEmpty()
                postDao.insert(posts.map(PostEntity::fromDto).map {
                    it.copy(hidden = true)
                })
//                postDao.readAll(latestId)
                emit(postDao.getUnreadPosts())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAllAsync() {
        try {
            val postsResponse = PostsApi.retrofitService.getAll()
            if (!postsResponse.isSuccessful) {
                throw HttpException(postsResponse)
            }
            val posts = postsResponse.body().orEmpty()
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: Exception) {
            throw NetworkErrorException()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long): Post {
        val like = PostsApi.retrofitService.likeById(id)
        if (!like.isSuccessful) {
            throw HttpException(like)
        }
        val likes = like.body() ?: throw NullPointerException()
        postDao.likeById(id)
        return likes
    }

    override suspend fun unlikeById(id: Long): Post {
        val like = PostsApi.retrofitService.likeById(id)
        if (!like.isSuccessful) {
            throw HttpException(like)
        }
        val likes = like.body() ?: throw NullPointerException()
        postDao.likeById(id)

        return likes
    }

    override suspend fun save(post: Post) {
        try {
            val save = PostsApi.retrofitService.save(post)
            if (!save.isSuccessful) {
                throw ApiError(save.code(), save.message())
            }
            val body = save.body() ?: throw ApiError(save.code(), save.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }
    }

    override suspend fun readAllPosts() {
        try {
            postDao.readAll()
        } catch (e: Exception) {
            throw UnknownError(e.message)
        }
    }

    override suspend fun saveWithAttachment(post: Post, mediaModel: MediaModel) {
        try {
            val media = upload(mediaModel)
            val save = PostsApi.retrofitService.save(
                post.copy(
                    attachment = Attachment(media.id, AttachmentTypes.IMAGE)
                )
            )
            if (!save.isSuccessful) {
                throw ApiError(save.code(), save.message())
            }
            val body = save.body() ?: throw ApiError(save.code(), save.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        val part = MultipartBody.Part.createFormData(
            "file", media.file.name, media.file.asRequestBody()
        )
        val response = PostsApi.retrofitService.uploadMedia(part)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return requireNotNull(response.body())
    }

    override suspend fun removeById(id: Long) {
        val remove = PostsApi.retrofitService.deleteById(id)
        if (!remove.isSuccessful) {
            throw HttpException(remove)
        }
        remove.body() ?: throw NullPointerException()
        postDao.removeById(id)
    }

}


