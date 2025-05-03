package com.team.feature_login.data.model

data class LoginResponse(
    val response: Response
) {
    data class Response(
        val state: Int,
        val error: String?,
        val result: TokenResult?
    ) {
        data class TokenResult(
            val token: String,
            val expires: String
        )
    }
} 