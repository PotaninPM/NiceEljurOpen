package com.team.feature_diary.di

import com.team.feature_diary.data.remote.DiaryApi
import com.team.feature_diary.data.repository.DiaryRepositoryImpl
import com.team.feature_diary.domain.repository.DiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiaryModule {

    @Provides
    @Singleton
    fun provideDiaryApi(retrofit: Retrofit): DiaryApi {
        return retrofit.create(DiaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(api: DiaryApi): DiaryRepository {
        return DiaryRepositoryImpl(api)
    }
} 