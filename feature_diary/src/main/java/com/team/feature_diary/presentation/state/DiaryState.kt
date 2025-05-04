package com.team.feature_diary.presentation.state

import com.team.feature_diary.domain.model.StudentInfo

data class DiaryState(
    val studentInfo: StudentInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)