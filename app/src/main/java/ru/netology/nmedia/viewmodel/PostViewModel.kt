package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    private val scope = MainScope()
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)


    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        scope.launch {
            try {
                _state.value = (FeedModelState(loading = true))
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() {
        scope.launch {
            try {
                _state.value = (FeedModelState(refreshing = true))
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun save() {
        scope.launch {
            try {
                _state.value = FeedModelState(loading = true)
                edited.value?.let {
                    repository.save(it)
                    _postCreated.value = Unit
                }
                edited.value = empty
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }

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
        scope.launch {
            try {
                _state.value = FeedModelState(loading = true)
               repository.likeById(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }


    fun unlikeById(id: Long) {
        scope.launch {
            try {
                _state.value = FeedModelState(loading = true)
                repository.unlikeById(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        scope.launch {
            try {
                _state.value = FeedModelState(loading = true)
                repository.removeById(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
        refreshPosts()
    }
}
