package com.team.feature_diary.data.model

data class DiaryResponse(
    val response: DiaryResponseData
)

data class DiaryResponseData(
    val state: Int,
    val error: String?,
    val result: DiaryResult?
)

data class DiaryResult(
    val days: List<DiaryDay>
)

data class DiaryDay(
    val date: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val name: String,
    val time: String,
    val homework: String?,
    val mark: String?
) 