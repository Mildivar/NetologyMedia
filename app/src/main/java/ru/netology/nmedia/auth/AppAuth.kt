package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _data: MutableStateFlow<AuthModel?>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0)

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
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _data.value = null
        prefs.edit { clear() }
    sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            val tokenNotNull = token ?: FirebaseMessaging.getInstance().token.await()
            try {
                PostsApi.retrofitService.sendPushToken(PushToken(tokenNotNull))
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppAuth? = null
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"

        fun getInstance(): AppAuth = synchronized(this) {
            requireNotNull(INSTANCE) {
                "You must call init(context:Context)"
            }
        }

        fun init(context: Context): AppAuth = synchronized(this) {
            INSTANCE ?: AppAuth(context).apply {
                INSTANCE = this
            }
        }
    }
}
