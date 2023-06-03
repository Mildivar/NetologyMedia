package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    appAuth: AppAuth,
    repository: PostRepositoryImpl,
    private val apiService: ApiService
) : ViewModel() {
    val data: LiveData<AuthModel?> = appAuth
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
        repository
//        PostRepositoryImpl(AppDb.getInstance(application).postDao())

    val authorized: Boolean
        get() = data.value != null

    fun authorization(login: String, password: String) {
        scope.launch {
            try {
                repository.authorization(login, password)
                val postsResponse = apiService.updateUser(login, password)
                val body = postsResponse.body() ?: throw ApiError(
                    postsResponse.code(),
                    postsResponse.message()
                )
                _signInApp.postValue(body)
//                _state.value = AuthState()
            } catch (e: Exception) {
                _state.value = AuthState(wrongAuth = true)
                println(e.message)

            }
        }
    }
}