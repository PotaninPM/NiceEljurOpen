package com.team.feature_messages.di

import com.team.feature_messages.data.remote.MessagesApi
import com.team.feature_messages.data.repository.MessagesRepositoryImpl
import com.team.feature_messages.domain.MessagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessagesModule {
    @Provides
    @Singleton
    fun provideMessagesApi(retrofit: Retrofit): MessagesApi {
        return retrofit.create(MessagesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMessagesRepository(api: MessagesApi): MessagesRepository {
        return MessagesRepositoryImpl(api)
    }
} 