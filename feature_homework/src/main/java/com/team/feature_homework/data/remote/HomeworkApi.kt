package com.team.feature_homework.data.remote

import com.team.common.ApiConstants
import com.team.feature_homework.data.model.HomeworkResponseAll
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeworkApi {
    @GET("apiv3/getdiary")
    suspend fun getHomework(
        @Query("student") student: String,
        @Query("days") days: String,
        @Query("rings") rings: Boolean = true,
        @Query("auth_token") authToken: String,
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT
    ): HomeworkResponseAll
} 