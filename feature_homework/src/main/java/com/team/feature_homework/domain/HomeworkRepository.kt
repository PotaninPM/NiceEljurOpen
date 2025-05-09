package com.team.feature_homework.domain

import com.team.feature_homework.data.model.HomeworkResponseAll

interface HomeworkRepository {
    suspend fun getHomework(
        student: String,
        days: String,
        authToken: String
    ): HomeworkResponseAll
} 