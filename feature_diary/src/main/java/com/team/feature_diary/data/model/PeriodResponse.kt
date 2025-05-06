package com.team.feature_diary.data.model

data class PeriodResponse(
    val response: PeriodResponseData
)

data class PeriodResponseData(
    val state: Int,
    val error: String?,
    val result: PeriodResult?
)

data class PeriodResult(
    val students: List<StudentPeriods>
)

data class StudentPeriods(
    val name: String,
    val title: String,
    val periods: List<Period>
)

data class Period(
    val name: String,
    val fullname: String,
    val disabled: Boolean,
    val ambigious: String?,
    val start: String,
    val end: String,
    val weeks: List<Week>
)

data class Week(
    val start: String,
    val end: String,
    val title: String
) 