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
    val students: Map<String, StudentDiary>
)

data class StudentDiary(
    val name: String,
    val title: String,
    val parent_signed: Boolean,
    val days: Map<String, DaySchedule>
)

data class DaySchedule(
    val name: String,
    val title: String,
    val alert: String?,
    val items: Map<String, Lesson>
)

data class Lesson(
    val homework: Map<String, HomeworkItem>,
    val assessments: List<Assessment>? = null,
    val files: List<LessonFile>,
    val resources: List<String>,
    val name: String,
    val lesson_id: String,
    val num: String,
    val room: String,
    val teacher: String,
    val sort: Int,
    val grp_short: String,
    val grp: String,
    val starttime: String,
    val endtime: String,
    val topic: String?
)

data class HomeworkItem(
    val value: String,
    val id: Int,
    val individual: Boolean
)

data class Assessment(
    val value: String,
    val countas: String,
    val color_hex: String?,
    val count: Boolean,
    val convert: Int,
    val lesson_id: String,
    val date: String,
    val nm: String,
    val control_type: String,
    val control_type_short: String,
    val weight: Int,
    val weight_float: String,
    val color: String,
    val comment: String
)

data class LessonFile(
    val toid: Int,
    val filename: String,
    val link: String
) 