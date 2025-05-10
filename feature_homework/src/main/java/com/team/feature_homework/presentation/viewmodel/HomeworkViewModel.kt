package com.team.feature_homework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.common.PreferencesManager
import com.team.feature_homework.data.model.HomeworkItem
import com.team.feature_homework.data.model.LessonFile
import com.team.feature_homework.domain.HomeworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeworkUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val homeworkByDay: Map<String, List<HomeworkItemModel>> = emptyMap()
)

data class HomeworkItemModel(
    val subject: String,
    val homework: List<HomeworkItem>,
    val teacher: String,
    val time: String,
    val topic: String?,
    val files: List<LessonFile>
)

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val repository: HomeworkRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeworkUiState())
    val uiState: StateFlow<HomeworkUiState> = _uiState

    fun loadHomework() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val studentId = preferencesManager.getStudentId()
                val authToken = preferencesManager.getAuthToken()

                if (studentId.isEmpty() || authToken.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Не удалось получить данные авторизации"
                        )
                    }
                    return@launch
                }

                val days = generateDateRange()
                val response = repository.getHomework(
                    student = studentId,
                    days = days,
                    authToken = authToken
                )

                if (response.response.state == 200 && response.response.result != null) {
                    val student = response.response.result.students[studentId]
                    if (student != null) {
                        val homeworkByDay = student.days.mapValues { (_, day) ->
                            day.items.values
                                .filter { it.homework.isNotEmpty() }
                                .sortedBy { it.sort }
                                .map { lesson ->
                                    HomeworkItemModel(
                                        subject = lesson.name,
                                        homework = lesson.homework.values.toList(),
                                        teacher = lesson.teacher,
                                        time = "${lesson.starttime} - ${lesson.endtime}",
                                        topic = lesson.topic,
                                        files = lesson.files
                                    )
                                }
                        }

                        _uiState.update { state ->
                            state.copy(
                                homeworkByDay = homeworkByDay,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Student not found"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.response.error ?: "Failed to load homework"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load homework"
                    )
                }
            }
        }
    }

    private fun generateDateRange(): String {
        val startDate = LocalDate.now()
        val endDate = startDate.plusWeeks(2)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return "${startDate.format(formatter)}-${endDate.format(formatter)}"
    }
} 