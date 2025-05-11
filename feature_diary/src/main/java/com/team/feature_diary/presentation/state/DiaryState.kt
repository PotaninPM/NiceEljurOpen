package com.team.feature_diary.presentation.state

import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.data.model.StudentPeriods
import com.team.feature_diary.domain.model.PersonInfo

data class DiaryState(
    val studentInfo: PersonInfo? = null,
    val weekDiary: StudentDiary? = null,
    val isLoading: Boolean = false,
    val periods: StudentPeriods? = null,
    val error: String? = null
)