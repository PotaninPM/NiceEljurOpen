package com.team.feature_diary.data.remote

import com.team.feature_diary.data.model.DiaryResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiaryApi {
    @GET("apiv3/diary")
    suspend fun getDiary(
        @Header("Authorization") token: String,
        @Header("User-Agent") userAgent: String = "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 4 Build/RP1A.200720.011)",
        @Query("devkey") devKey: String = "19c4bfc2705023fe080ce94ace26aec9",
        @Query("out_format") outFormat: String = "json",
        @Query("vendor") vendor: String = "hselyceum",
        @Query("weeks") weeks: Int = 2
    ): DiaryResponse
} 