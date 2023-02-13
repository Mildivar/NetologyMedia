package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = (FeedModel(loading = true))
        repository.getAll(object : PostRepository.Callback<List<Post>> {

            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun refreshPosts() {
        repository.getAll(object : PostRepository.Callback<List<Post>> {

            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
            _data.value = (FeedModel(loading = true))
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(data: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }

            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
             repository.likeById(id,  object : PostRepository.Callback<Post>{
                override fun onSuccess(data: Post) {
                    _data.postValue(
                        _data.value?.posts.orEmpty().let {
                            _data.value?.copy(
                                posts = it
                                    .map { post ->
                                        if (post.id == id) data else post
                                    }
                            )
                        }
                    )
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })

        }


    fun unlikeById(id: Long) {
        repository.unlikeById(id,  object : PostRepository.Callback<Post>{
            override fun onSuccess(data: Post) {
                _data.postValue(
                    _data.value?.posts.orEmpty().let {
                        _data.value?.copy(
                            posts = it
                                .map { post ->
                                    if (post.id == id) data else post
                                }
                        )
                    }
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        _data.value = (FeedModel(loading = true))
        repository.removeById(id,object :PostRepository.Callback<Unit>{
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )

            }

            override fun onError(e: Exception) {
                val old = _data.value?.posts.orEmpty()
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
        refreshPosts()

    }
}
