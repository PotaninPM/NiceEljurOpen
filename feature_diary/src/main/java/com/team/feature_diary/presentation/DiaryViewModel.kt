package com.team.feature_diary.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.feature_diary.domain.repository.DiaryRepository
import com.team.feature_diary.presentation.state.DiaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    var state by mutableStateOf(DiaryState())
        private set

    fun loadStudentInfo(token: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            repository.getStudentInfo(token)
                .onSuccess { studentInfo ->
                    state = state.copy(
                        isLoading = false,
                        studentInfo = studentInfo
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }
} 