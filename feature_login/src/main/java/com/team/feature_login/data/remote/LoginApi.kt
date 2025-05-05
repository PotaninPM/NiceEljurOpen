package com.team.feature_login.data.remote

import com.team.common.ApiConstants
import com.team.feature_login.data.model.LoginRequest
import com.team.feature_login.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("apiv3/auth")
    suspend fun login(
        @Body request: LoginRequest,
        @Header("User-Agent") userAgent: String = ApiConstants.USER_AGENT,
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("out_format") outFormat: String = ApiConstants.OUT_FORMAT,
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
    ): LoginResponse
}