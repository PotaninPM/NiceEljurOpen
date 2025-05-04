package com.team.feature_diary.data.model

import com.google.gson.annotations.SerializedName

data class DiaryResponse(
    val response: DiaryResponseData
)

data class DiaryResponseData(
    val state: Int,
    val error: String?,
    val result: DiaryResult?
)

data class DiaryResult(
    val roles: List<String>,
    val relations: Relations
)

data class Relations(
    val students: Map<String, Student>,
    val groups: Map<String, Group>
)

data class Student(
    val rules: List<String>,
    val rel: String,
    val name: String,
    val title: String,
    val lastname: String,
    val firstname: String,
    val gender: String,
    val `class`: String,
    val parallel: Int,
    val city: String
)

data class Group(
    val rules: List<String>,
    val rel: String,
    val name: String,
    val parallel: Int,
    val balls: Int,
    @SerializedName("hometeacher_id") val hometeacherId: String,
    @SerializedName("hometeacher_name") val hometeacherName: String,
    @SerializedName("hometeacher_lastname") val hometeacherLastname: String,
    @SerializedName("hometeacher_firstname") val hometeacherFirstname: String,
    @SerializedName("hometeacher_middlename") val hometeacherMiddlename: String
) 