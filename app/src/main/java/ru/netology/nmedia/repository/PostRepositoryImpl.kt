package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import kotlin.Exception


class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object : retrofit2.Callback<List<Post>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Post>>,
                    response: retrofit2.Response<List<Post>>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                    }
                    val posts = response.body()
                    if (posts == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }

                override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                    callback.onError(java.lang.Exception(t))
                }
            }
            )
    }

    override fun likeById(id: Long, callback: PostRepository.GetAllCallback<Post>) {

        PostApi.service.likeById(id)
            .enqueue(object : retrofit2.Callback<Post> {

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val posts = response.body()
                    if (posts == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }
            }
            )
    }

    override fun unlikeById(id: Long, callback: PostRepository.GetAllCallback<Post>) {

        PostApi.service.unlikeById(id)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val posts = response.body()
                    if (posts == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }
            }
            )
    }

    override fun save(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object : retrofit2.Callback<Post> {

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    val posts = response.body()
                    if (posts == null) {
                        callback.onError(RuntimeException("Body is null"))
                        return
                    }
                    callback.onSuccess(posts)
                }
            }
            )
    }

    override fun removeById(id: Long, callback: PostRepository.GetAllCallback<Unit>) {

        PostApi.service.deleteById(id)
            .enqueue(object : retrofit2.Callback<Unit> {

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>
                ) {
                    return
                }

            })
    }

}
