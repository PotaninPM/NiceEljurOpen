package com.team.feature_homework.data.repository

import com.team.feature_homework.data.model.HomeworkResponseAll
import com.team.feature_homework.data.remote.HomeworkApi
import com.team.feature_homework.domain.HomeworkRepository
import javax.inject.Inject

class HomeworkRepositoryImpl @Inject constructor(
    private val api: HomeworkApi
) : HomeworkRepository {
    override suspend fun getHomework(
        student: String,
        days: String,
        authToken: String
    ): HomeworkResponseAll {
        return api.getHomework(
            student = student,
            days = days,
            authToken = authToken
        )
    }
} 