package com.team.feature_diary.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.common.PreferencesManager
import com.team.feature_diary.domain.model.StudentInfo
import com.team.feature_diary.domain.repository.DiaryRepository
import com.team.feature_diary.presentation.state.DiaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var state by mutableStateOf(DiaryState())
        private set

    fun loadStudentInfo() {
        val token = preferencesManager.getAuthToken()
        val studentId = preferencesManager.getStudentId()
        val studentName = preferencesManager.getStudentName()
        val lastUpdateTime = preferencesManager.getLastUpdateTime()

        if (studentId.isNotEmpty() && studentName.isNotEmpty()) {
            state = state.copy(
                studentInfo = StudentInfo(
                    id = studentId,
                    name = studentName
                )
            )

            if (lastUpdateTime == 0L || hasOneMonthPassed(lastUpdateTime)) {
                updateStudentInfo(token)
            } else {
                loadDiary(token, studentId)
            }
        } else {
            updateStudentInfo(token)
        }
    }

    private fun updateStudentInfo(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true, error = null)

            repository.getStudentInfo(token)
                .onSuccess { studentInfo ->
                    preferencesManager.saveStudentId(studentInfo.id)
                    preferencesManager.saveStudentName(studentInfo.name)
                    preferencesManager.saveLastUpdateTime(System.currentTimeMillis())

                    state = state.copy(
                        studentInfo = studentInfo
                    )
                    loadDiary(token, studentInfo.id)
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }

    private fun loadDiary(token: String, studentId: String, days: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDiary(token, studentId, days)
                .onSuccess { diary ->
                    state = state.copy(
                        isLoading = false,
                        weekDiary = diary
                    )
                    loadPeriods(token)
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }

    private fun loadPeriods(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPeriods(token)
                .onSuccess { periods ->
                    state = state.copy(
                        isLoading = false,
                        periods = periods
                    )
                }
        }
    }

    private fun hasOneMonthPassed(lastUpdateTimeMillis: Long): Boolean {
        val lastUpdate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(lastUpdateTimeMillis),
            ZoneId.systemDefault()
        )
        val oneMonthLater = lastUpdate.plusMonths(1)
        return LocalDateTime.now().isAfter(oneMonthLater)
    }
} 