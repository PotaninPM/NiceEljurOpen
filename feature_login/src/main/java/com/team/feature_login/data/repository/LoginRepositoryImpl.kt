package com.team.feature_login.data.repository

import com.team.feature_login.data.model.LoginRequest
import com.team.feature_login.data.model.LoginResponse
import com.team.feature_login.data.remote.LoginApi
import com.team.feature_login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: LoginApi
) : LoginRepository {
    override suspend fun login(username: String, password: String): Result<LoginResponse.Response.Result> {
        return try {
            val response = api.login(LoginRequest(username, password))

            if (response.response.state == 200 && response.response.result != null) {
                Result.success(response.response.result)
            } else {
                Result.failure(Exception(response.response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 