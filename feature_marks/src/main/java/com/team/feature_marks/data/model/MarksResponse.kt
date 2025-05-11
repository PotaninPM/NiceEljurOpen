package com.team.feature_marks.data.model

import com.google.gson.annotations.SerializedName

data class MarksResponseAll(
    val response: MarksResponse
)

data class MarksResponse(
    val state: Int,
    val error: String?,
    val result: MarksResult?
)

data class MarksResult(
    val students: Map<String, StudentMarks>
)

data class StudentMarks(
    val name: String,
    val title: String,
    val lessons: List<LessonMarks>
)

data class LessonMarks(
    val average: String?,
    @SerializedName("average_convert") val averageConvert: Int?,
    val colorHex: String?,
    val name: String,
    @SerializedName("lesson_id") val lessonId: String,
    val marks: List<Mark>
)

data class Mark(
    val value: String,
    val countas: String?,
    val colorHex: String?,
    val count: Boolean,
    val comment: String?,
    @SerializedName("lesson_comment") val lessonComment: String?,
    val date: String,
    val convert: Int,
    @SerializedName("lesson_id") val lessonId: String?,
    val nm: String,
    val weight: Int?,
    @SerializedName("weight_float") val weightFloat: String?,
    val mtype: MarkType?
)

data class MarkType(
    val id: String,
    val type: String,
    val short: String
) 