package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _data: MutableStateFlow<AuthModel?>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0)

        if (token == null || id == 0L) {
            _data = MutableStateFlow(null)

            prefs.edit { clear() }
        } else {
            _data = MutableStateFlow(AuthModel(id, token))
        }
        sendPushToken()
    }

    val data = _data.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _data.value = AuthModel(id, token)
        prefs.edit {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _data.value = null
        prefs.edit { clear() }
        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                val entryPoint = EntryPointAccessors.fromApplication(context,AppAuthEntryPoint::class.java)
                entryPoint.getApiService().sendPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    companion object {
//        @Volatile
//        private var INSTANCE: AppAuth? = null
//        private const val TOKEN_KEY = "TOKEN_KEY"
//        private const val ID_KEY = "ID_KEY"
//
//        fun getInstance(): AppAuth = synchronized(this) {
//            requireNotNull(INSTANCE) {
//                "You must call init(context:Context)"
//            }
//        }
//
//        fun init(context: Context): AppAuth = synchronized(this) {
//            INSTANCE ?: AppAuth(context).apply {
//                INSTANCE = this
//            }
//        }
//    }
}
