package ru.netology.nmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds //связывает интерфейс и его реализацию(PostRepositoryImpl)
    fun bindPostRepository(
        impl: PostRepositoryImpl
    ): PostRepository
}