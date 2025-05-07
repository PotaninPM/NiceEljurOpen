package com.team.feature_diary.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.team.common.components.buttons.CustomButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarDialog(
    selectedDate: LocalDate = LocalDate.now(),
    onDataSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CalendarDialogContent(
            onBackClick = onDismissRequest,
            onDataSelected = onDataSelected
        )
    }
}

@Composable
private fun CalendarDialogContent(
    onBackClick: () -> Unit = {},
    onDataSelected: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else MaterialTheme.colorScheme.surface)
    ) {
        TopAppCalendarMenu(
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.width(26.dp))

        MonthSwitcherBar(

        )

        Spacer(modifier = Modifier.width(16.dp))

        CalendarDaysView(
            selectedDate = selectedDate,
            onDaySelected = {
                selectedDate = it
            }
        )

        CustomButton(
            text = "Готово",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
        )
    }
}

@Composable
private fun CalendarDaysView(
    selectedDate: LocalDate = LocalDate.now(),
    onDaySelected: (LocalDate) -> Unit
) {
    val dayNames = listOf(
        DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.TUESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.THURSDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.FRIDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.SATURDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    )

    val currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDateOffset = (firstDayOfMonth.dayOfWeek.value - 1) % 7

    val firstDateToShow = firstDayOfMonth.minusDays(firstDateOffset.toLong())
    val maxLines = if (firstDateOffset >= 5 && currentMonth.lengthOfMonth() > 30) 6 else 5

    Card(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 10.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DayOfWeekNames(dayNames)

            for (week in 0 until maxLines) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    for (day in 0 until 7) {
                        val currentDate = firstDateToShow.plusDays((week * 7 + day).toLong())
                        val isCurrentMonth = currentDate.month == currentMonth.month
                        val isSelected = currentDate.equals(selectedDate)
                        val isToday = currentDate.equals(LocalDate.now())

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(6.dp)
                                .let {
                                    if (isSelected) {
                                        it.background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                    } else {
                                        it
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentDate.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.surface
                                    isCurrentMonth -> LocalContentColor.current
                                    else -> Color.Gray.copy(alpha = 0.5f)
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable(enabled = true) {
                                        //selectedDate = currentDate
                                        onDaySelected(currentDate)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayOfWeekNames(dayNames: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayNames.forEach { day ->
            Text(
                text = day,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MonthSwitcherBar(
    modifier: Modifier = Modifier,
    onPreviousClick: (LocalDate) -> Unit = {},
    onNextClick: (LocalDate) -> Unit = {},
) {
    val currentTime by remember { mutableStateOf(LocalDate.now()) }
    val chosenMonth = currentTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val chosenYear = currentTime.year

    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*IconButton(
                onClick = onPreviousClick,
                modifier = Modifier
                    .padding(8.dp)
            ) {*/
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable {

                        }
                )
            //}

            Text(
                text = "$chosenMonth $chosenYear",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            /*IconButton(
                onClick = onNextClick,
                modifier = Modifier
                    .padding(8.dp)
            ) {*/
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable {

                        }
                )
            //}
        }
    }
}

@Composable
private fun TopAppCalendarMenu(
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = {
                onBackClick()
            },
            modifier = Modifier
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text = "Выберите дату",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = "Сегодня",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable {

                }
        )
    }
}

@Preview
@Composable
private fun CalendarDialogDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        CalendarDialogContent(
            onBackClick = {},
            onDataSelected = {}
        )
    }
}

@Preview
@Composable
private fun CalendarDialogLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        CalendarDialogContent(
            onBackClick = {},
            onDataSelected = {}
        )
    }
}

