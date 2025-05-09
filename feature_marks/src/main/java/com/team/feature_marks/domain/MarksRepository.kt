package com.team.feature_marks.domain

import com.team.feature_marks.data.model.MarksResponseAll

interface MarksRepository {
    suspend fun getMarks(
        student: String,
        days: String,
        authToken: String
    ): MarksResponseAll
} 