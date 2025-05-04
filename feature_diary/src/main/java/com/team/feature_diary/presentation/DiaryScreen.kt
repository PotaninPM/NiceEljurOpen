package com.team.feature_diary.presentation

import android.content.Context
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.feature_diary.R
import com.team.feature_diary.data.model.DaySchedule
import com.team.feature_diary.data.model.Lesson
import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.presentation.components.UserAvatarCircle
import com.team.feature_diary.presentation.state.DiaryState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
    val jwtToken = sharedPrefs.getString("jwt_token", "")
    val lastUpdateTime = sharedPrefs.getLong("last_student_info_update", 0)
    val studentId = sharedPrefs.getString("student_id", null)
    val studentName = sharedPrefs.getString("student_name", null)

    LaunchedEffect(Unit) {
        viewModel.loadStudentInfo(
            token = jwtToken!!,
            lastUpdateTime = lastUpdateTime,
            studentId = studentId,
            studentName = studentName
        )

        state.studentInfo?.let { studentInfo ->
            sharedPrefs.edit()
                .putLong("last_student_info_update", System.currentTimeMillis())
                .putString("student_id", studentInfo.id)
                .putString("student_name", studentInfo.name)
                .apply()
        }
    }

    if (!state.isLoading && state.weekDiary != null) {
        DiaryScreenContent(state)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(28.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Загрузка расписания...",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun DiaryScreenContent(
    state: DiaryState
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val chosenWeek = state.weekDiary?.let {
        val startDate = it.days.keys.first().let { key ->
            LocalDate.parse(key, DateTimeFormatter.BASIC_ISO_DATE)
        }
        val endDate = it.days.keys.last().let { key ->
            LocalDate.parse(key, DateTimeFormatter.BASIC_ISO_DATE)
        }.plusDays(1)
        "${startDate.format(DateTimeFormatter.ofPattern("dd.MM"))} - ${endDate.format(DateTimeFormatter.ofPattern("dd.MM"))}"
    } ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CustomTopAppBar(
            chosenWeek = chosenWeek,
            personName = state.studentInfo?.name,
            onBellClick = {}
        )

        Spacer(modifier = Modifier.size(4.dp))

        WeekCalendar(
            diary = state.weekDiary,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        SettingsBar()

        state.weekDiary?.let { diary ->
            val daySchedule = diary.days[selectedDate.format(DateTimeFormatter.BASIC_ISO_DATE)]

            if (daySchedule != null && daySchedule.alert == null) {
                LessonsList(daySchedule = daySchedule)
            } else {
                EmptySchedule()
            }
        }
    }
}

@Composable
fun SettingsBar() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

    }
}

@Composable
private fun WeekCalendar(
    diary: StudentDiary?,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentWeek = remember(selectedDate) {
        val monday = selectedDate.minusDays(selectedDate.dayOfWeek.value - 1L)
        (0..6).map { monday.plusDays(it.toLong()) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        currentWeek.forEach { date ->
            val day = diary?.days?.get(date.format(DateTimeFormatter.BASIC_ISO_DATE))
            val isVacation = day?.alert != null || date.dayOfWeek.value > 5

            DayItem(
                isVacation = isVacation,
                date = date,
                isSelected = date == selectedDate,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun DayItem(
    isVacation: Boolean = false,
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val figuresTextColor = if (isVacation) MaterialTheme.colorScheme.error else LocalContentColor.current
    val dayOfWeeksTextColor = if (isVacation) MaterialTheme.colorScheme.error else Color.Gray

    val today = LocalDate.now()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onDateSelected(date)
                }
            )
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            color = dayOfWeeksTextColor,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.size(6.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = if (isSelected) Color(0, 100, 255).copy(alpha = 0.8f) else Color.Transparent,
            modifier = Modifier
                .size(36.dp)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = if (today == date) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) else Color.Transparent,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else figuresTextColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun LessonsList(daySchedule: DaySchedule) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val sortedLessons = daySchedule.items.toList().sortedBy { it.second.sort }
        
        items(sortedLessons.size) { index ->
            val (_, lesson) = sortedLessons[index]
            LessonItem(lesson)

            if (index < sortedLessons.lastIndex) {
                val nextLesson = sortedLessons[index + 1].second
                val breakMinutes = calculateBreakMinutes(lesson.endtime, nextLesson.starttime)

                val period = "${lesson.endtime.substring(0, 5)} - ${nextLesson.starttime.substring(0, 5)}"
                if (breakMinutes > 0) {
                    BreakItem(breakMinutes, period = period)
                }
            }
        }
    }
}

private fun calculateBreakMinutes(endTime: String, startTime: String): Int {
    try {
        val (endHour, endMinute) = endTime.split(":").map { it.toInt() }
        val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
        
        val endMinutes = endHour * 60 + endMinute
        val startMinutes = startHour * 60 + startMinute
        
        return startMinutes - endMinutes
    } catch (e: Exception) {
        return 0
    }
}

@Composable
private fun LessonItem(lesson: Lesson) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${lesson.num} урок ${lesson.starttime}-${lesson.endtime}",
                    style = MaterialTheme.typography.titleMedium
                )
                if (lesson.room.isNotEmpty()) {
                    Text(
                        text = lesson.room,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = lesson.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (lesson.homework.isNotEmpty()) {
                Text(
                    text = "Домашнее задание",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                lesson.homework.values.forEach { homework ->
                    Text(
                        text = homework.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakItem(
    breakMinutes: Int,
    period: String = "00:00-00:00",
) {
    val text = "Перемена $breakMinutes минут"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (breakMinutes > 0) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Text(
                text = period,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun EmptySchedule() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Нет уроков",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CustomTopAppBar(
    chosenWeek: String = "21.04 - 27.04",
    personName: String? = "None",
    onBellClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 10.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserAvatarCircle(personName)

        WeekChooserList(
            chosenWeek = chosenWeek,
            onWeekChosen = { week ->
                Log.d("DiaryScreen", "Week chosen: $week")
            }
        )

        IconButton(
            onClick = {

            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bell_24px),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp),
            )
        }
    }

}

@Composable
fun WeekChooserList(
    chosenWeek: String = "21.04 - 27.04",
    availableWeeks: List<String> = listOf("21.04 - 27.04", "21.04 - 27.04", "21.04 - 27.04"),
    onWeekChosen: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = chosenWeek,
                fontWeight = FontWeight.SemiBold,
            )

            /*Spacer(modifier = Modifier.size(8.dp))

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            expanded = !expanded
                            onWeekChosen(currentWeek)
                        }
                    )
            )*/
        }
    }
}

