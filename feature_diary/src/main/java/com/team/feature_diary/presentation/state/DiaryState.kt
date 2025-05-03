package com.team.feature_diary.presentation.state

import com.team.feature_diary.data.model.DiaryDay

data class DiaryState(
    val days: List<DiaryDay> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 