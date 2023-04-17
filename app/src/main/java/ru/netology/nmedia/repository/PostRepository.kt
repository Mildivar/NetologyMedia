package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAllAsync()
    suspend fun likeById(id: Long):Post
    suspend fun unlikeById(id: Long):Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}
