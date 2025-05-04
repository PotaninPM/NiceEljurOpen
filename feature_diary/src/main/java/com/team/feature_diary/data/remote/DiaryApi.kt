package com.team.feature_diary.data.remote

import com.team.feature_diary.data.model.DiaryResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiaryApi {
    @GET("apiv3/getrules")
    suspend fun getDiary(
        @Header("User-Agent") userAgent: String = "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 4 Build/RP1A.200720.011)",
        @Header("Cookie") cookie: String = "",
        @Query("devkey") devKey: String = "19c4bfc2705023fe080ce94ace26aec9",
        @Query("out_format") outFormat: String = "json",
        @Query("auth_token") authToken: String = "",
        @Query("vendor") vendor: String = "hselyceum",
    ): DiaryResponse
} 