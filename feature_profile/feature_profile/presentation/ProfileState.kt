package com.niceeljur.feature_profile.presentation

import com.niceeljur.feature_profile.domain.model.Profile

data class ProfileState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 