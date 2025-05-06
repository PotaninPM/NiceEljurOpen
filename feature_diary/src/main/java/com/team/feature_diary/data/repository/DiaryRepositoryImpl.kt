package com.team.feature_diary.data.repository

import android.util.Log
import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.data.model.StudentPeriods
import com.team.feature_diary.data.remote.DiaryApi
import com.team.feature_diary.domain.model.StudentInfo
import com.team.feature_diary.domain.repository.DiaryRepository
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val api: DiaryApi
) : DiaryRepository {
    override suspend fun getStudentInfo(token: String): Result<StudentInfo> {
        return try {
            val response = api.getRules(authToken = token)

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

    override suspend fun getDiary(token: String, studentId: String, days: String?): Result<StudentDiary> {
        return try {
            val response = if (days != null) {
                api.getDiary(authToken = token, days = days, student = studentId)
            } else {
                api.getDiary(authToken = token, student = studentId)
            }

            if (response.response.state == 200 && response.response.result != null) {
                val student = response.response.result.students.values.firstOrNull()

                if (student != null) {
                    Result.success(student)
                } else {
                    Result.failure(Exception("No diary information found"))
                }
            } else {
                Result.failure(Exception(response.response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.d("INFOG2", e.toString())
            Result.failure(e)
        }
    }

    override suspend fun getPeriods(token: String): Result<StudentPeriods> {
        return try {
            val response = api.getPeriods(authToken = token)

            if (response.response.state == 200 && response.response.result != null) {
                val student = response.response.result.students.firstOrNull()

                if (student != null) {
                    Result.success(student)
                } else {
                    Result.failure(Exception("No periods information found"))
                }
            } else {
                Result.failure(Exception(response.response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 