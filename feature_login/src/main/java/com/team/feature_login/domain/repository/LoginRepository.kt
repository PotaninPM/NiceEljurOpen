package com.team.feature_login.domain.repository

import com.team.feature_login.data.model.LoginResponse

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse.Response.TokenResult>
}