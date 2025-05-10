package com.team.feature_diary.presentation.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team.feature_diary.R
import com.team.feature_diary.data.model.DaySchedule
import com.team.feature_diary.data.model.HomeworkItem
import com.team.feature_diary.data.model.Lesson
import com.team.feature_diary.data.model.StudentDiary
import com.team.feature_diary.data.model.StudentPeriods
import com.team.feature_diary.presentation.components.CalendarDialog
import com.team.feature_diary.presentation.components.UserAvatarCircle
import com.team.feature_diary.presentation.state.DiaryState
import com.team.feature_diary.presentation.viewModels.DiaryViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun DiaryScreen(
    onProfileIconClick: () -> Unit = {},
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val state = viewModel.state

    var calenderClicked by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        viewModel.loadStudentInfo()
    }
    if (calenderClicked) {
        CalendarDialog(
            selectedDate = selectedDate,
            onDismissRequest = {
                calenderClicked = !calenderClicked
            },
            onDataSelected = {

            }
        )
    } else {
        DiaryScreenContent(
            state = state,
            onProfileIconClick = onProfileIconClick,
            onCalendarClick = {
                calenderClicked = !calenderClicked
            },
            onDateSelected = {
                selectedDate = it
            }
        )
    }
}

@Composable
private fun DiaryScreenContent(
    state: DiaryState,
    onDateSelected: (LocalDate) -> Unit,
    onProfileIconClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val defaultDate = LocalDate.now().let {
        it.minusDays(it.dayOfWeek.value.toLong() - 1).let { startOfWeek ->
            val endOfWeek = startOfWeek.plusDays(6)
            "${startOfWeek.format(DateTimeFormatter.ofPattern("dd.MM"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("dd.MM"))}"
        }
    }

    val chosenWeek = state.weekDiary?.let {
        val startDate = it.days.keys.first().let { key ->
            LocalDate.parse(key, DateTimeFormatter.BASIC_ISO_DATE)
        }
        val endDate = it.days.keys.last().let { key ->
            LocalDate.parse(key, DateTimeFormatter.BASIC_ISO_DATE)
        }.plusDays(1)
        "${startDate.format(DateTimeFormatter.ofPattern("dd.MM"))} - ${endDate.format(DateTimeFormatter.ofPattern("dd.MM"))}"
    } ?: defaultDate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        CustomTopAppBar(
            chosenWeek = chosenWeek,
            personName = state.studentInfo?.name,
            onProfileIconClick = onProfileIconClick,
            onBellClick = {

            },
            onCalendarClick = { onCalendarClick() },
            periodsInfo = state.periods
        )

        Spacer(modifier = Modifier.size(4.dp))

        WeekCalendar(
            diary = state.weekDiary,
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                onDateSelected(it)
            },
            onWeekChanged = { startOfWeek, endOfWeek ->

            }
        )

        if (!state.isLoading && state.weekDiary != null) {
            state.weekDiary.let { diary ->
                val daySchedule = diary.days[selectedDate.format(DateTimeFormatter.BASIC_ISO_DATE)]

                if (daySchedule != null && daySchedule.alert == null) {
                    LessonsList(daySchedule = daySchedule, selectedDate = selectedDate)
                } else {
                    EmptySchedule()
                }
            }
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
}

@Composable
fun WeekCalendar(
    diary: StudentDiary?,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onWeekChanged: (startOfWeek: LocalDate, endOfWeek: LocalDate) -> Unit
) {
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = {
        Int.MAX_VALUE
    })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .map { page ->
                val weekOffset = page - initialPage
                val anyDayInWeek = selectedDate.plusWeeks(weekOffset.toLong())
                val monday = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val sunday = monday.plusDays(6)
                monday to sunday
            }
            .distinctUntilChanged()
            .collect { (monday, sunday) ->
                onWeekChanged(monday, sunday)
            }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val weekOffset = page - initialPage
        val anyDayInWeek = selectedDate.plusWeeks(weekOffset.toLong())
        val monday = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekDays = (0..6).map { monday.plusDays(it.toLong()) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { date ->
                val day = diary?.days?.get(date.format(DateTimeFormatter.BASIC_ISO_DATE))
                val isVacation = day?.alert != null || date.dayOfWeek.value > 5

                DayItem(
                    isVacation = isVacation,
                    date = date,
                    isSelected = date == selectedDate,
                    onDateSelected = { onDateSelected(it) }
                )
            }
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
    val today = LocalDate.now()

    val figuresTextColor = if (isSelected) {
        Color.White
    } else {
        if (today == date) {
            Color(0, 100, 255).copy(alpha = 0.6f)
        } else {
            if (isVacation) {
                MaterialTheme.colorScheme.error
            } else {
                LocalContentColor.current
            }
        }
    }

    val textFontWeight = if (isSelected) {
        FontWeight.Bold
    } else if (today == date) {
       FontWeight.SemiBold
    } else {
        FontWeight.Normal
    }

    val dayOfWeeksTextColor = if (isVacation) MaterialTheme.colorScheme.error else Color.Gray
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
                .padding(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    color = figuresTextColor,
                    fontWeight = textFontWeight
                )
            }
        }
    }
}

@Composable
private fun LessonsList(daySchedule: DaySchedule, selectedDate: LocalDate) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val sortedLessons = daySchedule.items.toList().sortedBy { it.second.sort }
        
        items(sortedLessons.size) { index ->
            val (_, lesson) = sortedLessons[index]
            Log.d("INFOG2", selectedDate.toString())

            LessonItem(lesson, selectedDate)

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
private fun LessonItem(lesson: Lesson, day: LocalDate) {
    val currentTime = LocalDate.now().minusDays(100)

    val textColor = if (currentTime.isAfter(day)) {
        Color.Gray
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val indicatorColor = if (currentTime.isAfter(day)) {
        Color.Gray
    } else {
        Color(23, 173, 252, 255)
    }

    val homeworkTextColor = if (currentTime.isAfter(day)) {
        Color.Gray
    } else {
        MaterialTheme.colorScheme.primary
    }

    val homeworks = lesson.homework.values.toList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
       Row(
           modifier = Modifier
               .fillMaxWidth()
       ) {
           LessonIndicator(indicatorColor)

           Column(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(16.dp)
           ) {
               Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.spacedBy(8.dp)
               ) {
                   Text(
                       text = "${lesson.num} урок ${lesson.starttime.substring(0, 5)} - ${lesson.endtime.substring(0, 5)}",
                       style = MaterialTheme.typography.bodyMedium,
                       color = textColor
                   )
                   if (lesson.room.isNotEmpty()) {
                       Text(
                           text = "•",
                           color = MaterialTheme.colorScheme.onSurface,
                           fontWeight = FontWeight.Bold,
                           style = MaterialTheme.typography.bodyMedium
                       )
                       Text(
                           text = lesson.room,
                           color = indicatorColor,
                           fontWeight = FontWeight.SemiBold,
                           style = MaterialTheme.typography.bodyMedium
                       )
                   }
               }

               Text(
                   text = lesson.name,
                   style = MaterialTheme.typography.titleMedium,
                   fontWeight = FontWeight.Bold,
                   color = textColor,
                   modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
               )

               HomeworkAndFiles(lesson, homeworkTextColor, homeworks)

               /*MarkCard(
                   grade = 5,
                   weight = 0.25f,
               )*/
           }
       }

    }
}

@Composable
private fun HomeworkAndFiles(
    lesson: Lesson,
    homeworkTextColor: Color,
    homeworks: List<HomeworkItem>
) {
    if (lesson.homework.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home_24px),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                tint = homeworkTextColor
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Домашнее задание",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = homeworkTextColor
            )
        }

        Column(
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            homeworks.forEachIndexed { index, hw ->
                if (hw.value.isNotEmpty()) {
                    Text(
                        text = "• ${hw.value}",
                        color = homeworkTextColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal
                    )
                    if (index < homeworks.lastIndex) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LessonIndicator(color: Color) {
    Column {
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .width(8.dp)
                .height(55.dp)
                .background(
                    color = color,
                    shape  = RoundedCornerShape(
                        topEnd = 200.dp,
                        bottomEnd = 200.dp
                    )
                )
        )
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
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (breakMinutes > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sprint_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

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
    chosenWeek: String,
    personName: String? = "None",
    onBellClick: () -> Unit = {},
    onProfileIconClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    periodsInfo: StudentPeriods?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 6.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeekChooserList(
            chosenWeek = chosenWeek,
            onWeekChosen = { week ->
                Log.d("DiaryScreen", "Week chosen: $week")
            },
            onCalendarClick = onCalendarClick,
            periodsInfo = periodsInfo
        )

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BellIcon()

            UserAvatarCircle(
                title = personName,
                onIconClick = onProfileIconClick
            )
        }
    }

}

@Composable
private fun BellIcon(somethingNew: Boolean = true) {
    BadgedBox(
        badge = {
            if (somethingNew) {
                Badge(
                    modifier = Modifier,
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            }
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.bell_24px),
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {

                    }
                )
        )
    }

}

@Composable
fun WeekChooserList(
    chosenWeek: String = "21.04 - 27.04",
    periodsInfo: StudentPeriods?,
    onCalendarClick: () -> Unit = {},
    onWeekChosen: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val periods = periodsInfo?.periods?.map { it.name to it.weeks } ?: emptyList()

    OutlinedCard(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        onClick = {
//            expanded = !expanded
            onCalendarClick()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_month_24px),
                contentDescription = null,
                modifier = Modifier
                    .size(21.dp)
            )

            Spacer(modifier = Modifier.width(3.dp))

            Text(
                text = chosenWeek,
                fontWeight = FontWeight.SemiBold,
            )

            /*Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )*/
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        periods.forEach { (periodName, weeks) ->
            val periodWeeks = weeks.map { it.start + " - " + it.end }

            periodWeeks.forEach { week ->
                val startOfWeek = week.split(" - ")[0]
                val endOfWeek = week.split(" - ")[1]

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "$startOfWeek - $endOfWeek",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onWeekChosen(week)
                        expanded = false
                    }
                )
            }
        }
    }
}

