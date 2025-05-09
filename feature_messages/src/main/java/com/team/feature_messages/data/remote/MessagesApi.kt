package com.team.feature_messages.data.remote

import com.team.feature_messages.data.model.MessagesResponseInfoAll
import com.team.common.ApiConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface MessagesApi {
    @GET("apiv3/getmessages")
    suspend fun getMessages(
        @Query("folder") folder: String,
        @Query("unreadonly") unreadOnly: String = "no",
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("filter") filter: String? = null,
        @Query("auth_token") authToken: String? = null,
        @Query("devkey") devKey: String = ApiConstants.DEV_KEY,
        @Query("vendor") vendor: String = ApiConstants.VENDOR,
        @Query("out_format") outFormat: String = "json"
    ): MessagesResponseInfoAll
}