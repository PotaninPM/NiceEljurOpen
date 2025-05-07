package com.niceeljur.feature_profile.domain.repository

import com.niceeljur.feature_profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<Profile>
    suspend fun updateProfile(profile: Profile): Result<Unit>
} 