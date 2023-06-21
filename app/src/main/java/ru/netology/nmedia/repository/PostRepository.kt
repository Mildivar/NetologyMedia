package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.MediaModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
    fun getNewerCount(latestId: Long): Flow<Int> //подписка
    suspend fun getAllAsync()
    suspend fun likeById(id: Long): Post
    suspend fun unlikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun readAllPosts()
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun authorization(login: String, password: String)
}
