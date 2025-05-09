package com.team.feature_homework.di

import com.team.feature_homework.data.remote.HomeworkApi
import com.team.feature_homework.data.repository.HomeworkRepositoryImpl
import com.team.feature_homework.domain.HomeworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkModule {
    @Provides
    @Singleton
    fun provideHomeworkApi(retrofit: Retrofit): HomeworkApi {
        return retrofit.create(HomeworkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeworkRepository(api: HomeworkApi): HomeworkRepository {
        return HomeworkRepositoryImpl(api)
    }
} 