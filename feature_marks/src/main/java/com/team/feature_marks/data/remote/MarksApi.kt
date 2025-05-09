package com.team.feature_marks.data.remote

import com.team.common.ApiConstants
import com.team.feature_marks.data.model.MarksResponseAll
import retrofit2.http.GET
import retrofit2.http.Query

interface MarksApi {
    @GET("apiv3/getmarks")
    suspend fun getMarks(
        @Query("student") student: String,
        @Query("days") days: String,
        @Query("auth_token") authToken: String,
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT
    ): MarksResponseAll
} 