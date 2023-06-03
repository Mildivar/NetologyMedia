package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) //данная зависимость предоставлена на уровне всего приложения
@Module
class ModuleDb {
    @Singleton
    @Provides //создание вручную экземпляра объекта
    fun provideDb(
        @ApplicationContext //ответственность за то, как контекст попадает в даннцю ф-ю переходит на библиотеку
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDb
    ): PostDao = appDb.postDao()
}