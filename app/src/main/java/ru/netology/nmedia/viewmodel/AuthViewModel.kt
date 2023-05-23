package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthState
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val data: LiveData<AuthModel?> = AppAuth.getInstance()
        .data
        .asLiveData()

    private val scope = MainScope()
    private val _state = MutableLiveData<AuthState>()
    val state: LiveData<AuthState>
        get() = _state
    private val _signInApp = SingleLiveEvent<AuthModel>()
    val signInApp: LiveData<AuthModel>
        get() = _signInApp


    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())

    val authorized: Boolean
        get() = data.value != null

    fun authorization(login: String, password: String) {
        scope.launch {
            try{
                repository.authorization(login, password)
                val postsResponse = PostsApi.retrofitService.updateUser(login, password)
                val body = postsResponse.body() ?: throw ApiError(
                    postsResponse.code(),
                    postsResponse.message()
                )
                _signInApp.postValue(body)
//                _stateSignIn.value = SignInModelState()
            }catch (e:Exception){
                _state.value = AuthState(wrongAuth = true)
            }
        }
    }
}