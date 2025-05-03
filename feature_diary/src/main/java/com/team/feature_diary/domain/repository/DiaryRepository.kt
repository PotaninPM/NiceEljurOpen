package com.team.feature_diary.domain.repository

import com.team.feature_diary.data.model.DiaryResult

interface DiaryRepository {
    suspend fun getDiary(token: String): Result<DiaryResult>
} 