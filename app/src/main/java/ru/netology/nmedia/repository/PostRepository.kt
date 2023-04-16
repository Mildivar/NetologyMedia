package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun likeById(id: Long,callback:GetAllCallback<Post>)
    fun unlikeById(id: Long,callback:GetAllCallback<Post>)
    fun save(post: Post, callback:GetAllCallback<Post>)
    fun removeById(id: Long, callback:GetAllCallback<Unit>)

    fun getAllAsync(callback:GetAllCallback<List<Post>>)

    interface GetAllCallback<T>{
        fun onSuccess(data:T)
        fun onError(e:Exception)
    }

}
