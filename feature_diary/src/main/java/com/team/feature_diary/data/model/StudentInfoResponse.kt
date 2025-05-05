package com.team.feature_diary.data.model

import com.google.gson.annotations.SerializedName

data class StudentInfoResponse(
    val response: StudentInfoResponseData
)

data class StudentInfoResponseData(
    val state: Int,
    val error: String?,
    val result: StudentInfoResult?
)

data class StudentInfoResult(
    val roles: List<String>,
    val relations: StudentInfoRelations
)

data class StudentInfoRelations(
    val students: Map<String, StudentInfoDetails>,
    val groups: Map<String, StudentInfoGroup>
)

data class StudentInfoDetails(
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

data class StudentInfoGroup(
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