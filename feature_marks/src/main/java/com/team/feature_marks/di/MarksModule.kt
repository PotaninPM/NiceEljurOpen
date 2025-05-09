package com.team.feature_marks.di

import com.team.feature_marks.data.remote.MarksApi
import com.team.feature_marks.data.repository.MarksRepositoryImpl
import com.team.feature_marks.domain.MarksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarksModule {
    @Provides
    @Singleton
    fun provideMarksApi(retrofit: Retrofit): MarksApi {
        return retrofit.create(MarksApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMarksRepository(api: MarksApi): MarksRepository {
        return MarksRepositoryImpl(api)
    }
} 