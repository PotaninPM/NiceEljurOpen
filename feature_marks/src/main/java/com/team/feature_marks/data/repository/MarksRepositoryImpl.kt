package com.team.feature_marks.data.repository

import com.team.feature_marks.data.model.MarksResponseAll
import com.team.feature_marks.data.remote.MarksApi
import com.team.feature_marks.domain.MarksRepository
import javax.inject.Inject

class MarksRepositoryImpl @Inject constructor(
    private val api: MarksApi
) : MarksRepository {
    override suspend fun getMarks(
        student: String,
        days: String,
        authToken: String
    ): MarksResponseAll {
        return api.getMarks(
            student = student,
            days = days,
            authToken = authToken
        )
    }
} 