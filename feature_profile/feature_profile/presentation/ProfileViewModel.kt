package com.niceeljur.feature_profile.presentation

import androidx.lifecycle.ViewModel
import com.niceeljur.feature_profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {
    // ViewModel implementation will be added later
} 