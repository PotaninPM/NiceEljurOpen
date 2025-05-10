package com.team.feature_homework.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.common.functions.centerEllipsis
import com.team.feature_homework.R
import com.team.feature_homework.presentation.viewmodel.HomeworkItemModel
import com.team.feature_homework.presentation.viewmodel.HomeworkViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeworkScreen(
    viewModel: HomeworkViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomework()
    }

/*    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Домашние задания") },
            )
        }
    ) { _ ->*/
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                HomeworkList(
                    homeworkByDay = uiState.homeworkByDay,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    //}
}

@Composable
private fun HomeworkList(
    homeworkByDay: Map<String, List<HomeworkItemModel>>,
    modifier: Modifier = Modifier
) {
    val sortedDates = remember(homeworkByDay) {
        homeworkByDay.keys
            .sortedBy { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyyMMdd")) }
            .filter {
                homeworkByDay[it]?.isNotEmpty() == true
            }
    }

    LazyColumn(
        modifier = modifier,
    ) {
        items(sortedDates) { date ->
            DayHomeworkCard(
                date = date,
                homework = homeworkByDay[date] ?: emptyList()
            )
        }
    }
}

@Composable
private fun DayHomeworkCard(
    date: String,
    homework: List<HomeworkItemModel>
) {
    val formattedDate = remember(date) {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
            .format(DateTimeFormatter.ofPattern("d MMMM"))
    }

    val daysAfterThis = remember(date) {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
            .compareTo(LocalDate.now())
    }

    val daysAfterThisText = if (daysAfterThis == 0) {
        "Сегодня"
    } else if (daysAfterThis < 0) {
        "${-daysAfterThis} дней назад"
    } else if (daysAfterThis == 1) {
        "Завтра"
    } else {
        "Через $daysAfterThis дней(-я)"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = daysAfterThisText,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            homework.forEach { item ->
                HomeworkItemCard(item = item)

                if (item != homework.last()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
@Composable
fun HomeworkItemCard(item: HomeworkItemModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = item.subject,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        item.homework.forEach { homework ->
            var done by remember(homework) { mutableStateOf(false) }
            var lineCount by remember(homework) { mutableIntStateOf(0) }

            val alignment = if (lineCount <= 1) {
                Alignment.CenterVertically
            } else {
                Alignment.Top
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { done = !done }
                    .padding(vertical = 4.dp),
                verticalAlignment = alignment,
            ) {
                CustomCheckBoxItem(
                    checked = done,
                    onCheckedChange = { done = it }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = homework.value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (done)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (done)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    modifier = Modifier
                        .weight(1f),
                    onTextLayout = { textLayoutResult ->
                        lineCount = textLayoutResult.lineCount
                    }
                )
            }

            if (homework != item.homework.last()) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (item.files.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            ) {
                item.files.forEach { file ->
                    val fileName = file.filename.centerEllipsis(20)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.description_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            buildAnnotatedString {
                                withLink(
                                    LinkAnnotation.Url(
                                        file.link,
                                        TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
                                    )
                                ) {
                                    append(fileName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCheckBoxItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val size = 30.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (checked) Color(0, 100, 255).copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = 2.dp,
                color = if (checked) Color(0, 100, 255).copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.7f)
            )
        }
    }
}

