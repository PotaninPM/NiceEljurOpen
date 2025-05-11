package com.team.feature_diary.domain.repository

import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.data.model.StudentPeriods
import com.team.feature_diary.domain.model.PersonInfo

interface DiaryRepository {
    suspend fun getStudentInfo(token: String): Result<PersonInfo>
    suspend fun getDiary(token: String, studentId: String, days: String? = null): Result<StudentDiary>
    suspend fun getPeriods(token: String): Result<StudentPeriods>
} 