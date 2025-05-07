package com.team.feature_diary.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarDialog(
    onDataSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CalendarDialogContent(
            onBackClick = onDismissRequest,
        )
    }
}

@Composable
private fun CalendarDialogContent(
    onBackClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppCalendarMenu(
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.width(26.dp))

        MonthSwitcherBar()

        Spacer(modifier = Modifier.width(16.dp))

        CalendarDaysView(
            onDaySelected = {

            }
        )
    }
}

@Composable
private fun CalendarDaysView(
    onDaySelected: (String) -> Unit
) {
    val currentDate = LocalDate.now()


    Card(
        modifier = Modifier
    ) {

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
            .padding(horizontal = 12.dp),
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
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Готово",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
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
        CalendarDialogContent()
    }
}

@Preview
@Composable
private fun CalendarDialogLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        CalendarDialogContent()
    }
}

