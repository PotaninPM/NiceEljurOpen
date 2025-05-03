package com.team.feature_diary.data.repository

import com.team.feature_diary.data.model.DiaryResult
import com.team.feature_diary.data.remote.DiaryApi
import com.team.feature_diary.domain.repository.DiaryRepository
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val api: DiaryApi
) : DiaryRepository {
    override suspend fun getDiary(token: String): Result<DiaryResult> {
        return try {
            val response = api.getDiary(token = "Bearer $token")

            if (response.response.state == 200 && response.response.result != null) {
                Result.success(response.response.result)
            } else {
                Result.failure(Exception(response.response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 