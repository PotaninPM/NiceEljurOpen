package com.team.feature_diary.presentation.state

import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.domain.model.StudentInfo

data class DiaryState(
    val studentInfo: StudentInfo? = null,
    val weekDiary: StudentDiary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)