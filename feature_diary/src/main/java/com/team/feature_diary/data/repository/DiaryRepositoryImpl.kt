package com.team.feature_diary.data.repository

import com.team.feature_diary.data.remote.DiaryApi
import com.team.feature_diary.domain.model.StudentInfo
import com.team.feature_diary.domain.repository.DiaryRepository
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val api: DiaryApi
) : DiaryRepository {
    override suspend fun getStudentInfo(token: String): Result<StudentInfo> {
        return try {
            val response = api.getDiary(authToken = token)

            if (response.response.state == 200 && response.response.result != null) {
                val student = response.response.result.relations.students.entries.firstOrNull()
                
                if (student != null) {
                    Result.success(
                        StudentInfo(
                            id = student.key,
                            name = student.value.title
                        )
                    )
                } else {
                    Result.failure(Exception("No student information found"))
                }
            } else {
                Result.failure(Exception(response.response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 