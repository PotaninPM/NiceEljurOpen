package com.team.feature_login.data.remote

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
        @Header("User-Agent") userAgent: String = "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 4 Build/RP1A.200720.011)",
        @Query("devkey") devKey: String = "19c4bfc2705023fe080ce94ace26aec9",
        @Query("out_format") outFormat: String = "json",
        @Query("vendor") vendor: String = "hselyceum"
    ): LoginResponse
} 