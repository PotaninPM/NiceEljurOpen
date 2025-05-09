package com.team.feature_homework.data.model

data class HomeworkResponseAll(
    val response: HomeworkResponse
)

data class HomeworkResponse(
    val state: Int,
    val error: String?,
    val result: HomeworkResult?
)

data class HomeworkResult(
    val students: Map<String, Student>
)

data class Student(
    val name: String,
    val title: String,
    val parentSigned: Boolean,
    val days: Map<String, Day>
)

data class Day(
    val name: String,
    val title: String,
    val items: Map<String, LessonItem>
)

data class LessonItem(
    val homework: Map<String, HomeworkItem>,
    val files: List<LessonFile>,
    val resources: List<String>,
    val name: String,
    val lessonId: String,
    val num: String,
    val room: String?,
    val teacher: String,
    val sort: Int,
    val grpShort: String?,
    val grp: String?,
    val starttime: String,
    val endtime: String,
    val topic: String?
)

data class LessonFile(
    val toid: Int,
    val filename: String,
    val link: String
)

data class HomeworkItem(
    val value: String,
    val id: Int,
    val individual: Boolean
)