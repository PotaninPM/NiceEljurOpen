package com.team.feature_diary.data.remote

import com.team.common.ApiConstants
import com.team.feature_diary.data.model.DiaryResponse
import com.team.feature_diary.data.model.PeriodResponse
import com.team.feature_diary.data.model.StudentInfoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface DiaryApi {
    @GET("apiv3/getrules")
    suspend fun getRules(
        @Header("User-Agent") userAgent: String = ApiConstants.USER_AGENT,
        @Header("Cookie") cookie: String = "",
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT,
        @Query("auth_token") authToken: String = "",
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
    ): StudentInfoResponse

    @GET("apiv3/getdiary")
    suspend fun getDiary(
        @Header("User-Agent") userAgent: String = ApiConstants.USER_AGENT,
        @Header("Cookie") cookie: String = "",
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT,
        @Query("auth_token") authToken: String = "",
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
        @Query("days") days: String = getDefaultWeekRange(),
        @Query("rings") rings: Int = 1,
        @Query("student") student: String
    ): DiaryResponse

    @GET("apiv3/getperiods")
    suspend fun getPeriods(
        @Header("User-Agent") userAgent: String = ApiConstants.USER_AGENT,
        @Query("weeks") weeks: Boolean = true,
        @Query("show_disabled") showDisabled: Boolean = true,
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT,
        @Query("auth_token") authToken: String = "",
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
    ): PeriodResponse

    companion object {
        fun getDefaultWeekRange(): String {
            val today = LocalDate.now()
            val monday = today.minusDays(today.dayOfWeek.value - 1L).minusWeeks(1)
            val sunday = monday.plusDays(6).plusWeeks(1)

            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            return "${monday.format(formatter)}-${sunday.format(formatter)}"
        }
    }
} 