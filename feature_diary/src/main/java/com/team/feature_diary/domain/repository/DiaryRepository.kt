package com.team.feature_diary.domain.repository

import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.domain.model.StudentInfo

interface DiaryRepository {
    suspend fun getStudentInfo(token: String): Result<StudentInfo>
    suspend fun getDiary(token: String, studentId: String, days: String? = null): Result<StudentDiary>
} 