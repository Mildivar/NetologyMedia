package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    authorId = 0L,
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository:PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val scope = MainScope()
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>> = appAuth.data.flatMapLatest { authState ->
        repository.data
            .map {posts ->
                posts.map {
                    it.copy(ownedByMe = authState?.id == it.authorId)
                }
            }
    }.flowOn(Dispatchers.Default)

    //как только что-то меняется - подписка на количество новых постов
    @OptIn(ExperimentalCoroutinesApi::class)
    val newerCount: Flow<LiveData<Int>> = data.mapLatest {
        val latestPostId = appAuth.data.firstOrNull()?.id ?: 0L
        repository.getNewerCount(latestPostId).asLiveData()
    }

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media


//    init {
//        loadPosts()
//    }

    fun changePhoto(file: File, uri: Uri) {
        _media.value = MediaModel(uri, file)
    }

    fun clearPhoto() {
        _media.value = null
    }

    fun readAllPosts() {
        scope.launch {
            try {
                repository.readAllPosts()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun loadPosts() {
        scope.launch {
            try {
                _state.value = (FeedModelState(loading = true))
//                repository.getAllAsync()
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
//                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun save() {
        edited.value?.let {
            scope.launch {
                try {
                    when (val media = media.value) {
                        null -> repository.save(it)
                        else -> repository.saveWithAttachment(it, media)
                    }
                    _state.value = FeedModelState(loading = true)
                    _postCreated.value = Unit
                    edited.value = empty
                    clearPhoto()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
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



