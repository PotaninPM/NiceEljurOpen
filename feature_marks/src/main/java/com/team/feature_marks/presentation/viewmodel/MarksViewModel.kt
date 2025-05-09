package com.team.feature_marks.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.feature_marks.data.model.LessonMarks
import com.team.feature_marks.data.model.Mark
import com.team.feature_marks.domain.MarksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class MarksDisplayMode {
    data object BySubject : MarksDisplayMode()
    data object ByDate : MarksDisplayMode()
}

data class MarksUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val displayMode: MarksDisplayMode = MarksDisplayMode.BySubject,
    val marksBySubject: List<LessonMarks> = emptyList(),
    val marksByDate: Map<String, List<Mark>> = emptyMap()
)

@HiltViewModel
class MarksViewModel @Inject constructor(
    private val repository: MarksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarksUiState())
    val uiState: StateFlow<MarksUiState> = _uiState

    fun loadMarks(authToken: String, studentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val days = generateDateRange()
                val response = repository.getMarks(
                    student = studentId,
                    days = days,
                    authToken = authToken
                )

                if (response.response.state == 200 && response.response.result != null) {
                    val student = response.response.result.students[studentId]
                    if (student != null) {
                        val marksByDate = student.lessons.flatMap { lesson ->
                            lesson.marks.map { mark ->
                                mark.copy(
                                    lessonComment = "${lesson.name}: ${mark.lessonComment ?: ""}"
                                )
                            }
                        }.groupBy { it.date }

                        _uiState.update { state ->
                            state.copy(
                                marksBySubject = student.lessons,
                                marksByDate = marksByDate,
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
                            error = response.response.error ?: "Failed to load marks"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MarksViewModel", "Error loading marks", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load marks"
                    )
                }
            }
        }
    }

    fun setDisplayMode(mode: MarksDisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
    }

    private fun generateDateRange(): String {
        val startDate = when {
            LocalDate.now().monthValue in 9..12 -> LocalDate.now().withMonth(9).withDayOfMonth(1)
            else -> LocalDate.now().withYear(LocalDate.now().year).withMonth(1).withDayOfMonth(5)
        }

        val endDate = when {
            LocalDate.now().monthValue in 9..12 -> startDate.withMonth(12).withDayOfMonth(30)
            else -> startDate.withMonth(5).withDayOfMonth(30)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return "${startDate.format(formatter)}-${endDate.format(formatter)}"
    }
} 