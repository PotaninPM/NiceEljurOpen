package com.team.feature_marks.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.common.components.UserInfoTopBar
import com.team.common.components.icons.PeriodsIcon
import com.team.feature_marks.data.model.LessonMarks
import com.team.feature_marks.data.model.Mark
import com.team.feature_marks.presentation.viewmodel.MarksDisplayMode
import com.team.feature_marks.presentation.viewmodel.MarksViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun MarksScreen(
    viewModel: MarksViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(Unit) {
        viewModel.loadMarks()
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setDisplayMode(
            when (pagerState.currentPage) {
                0 -> MarksDisplayMode.BySubject
                else -> MarksDisplayMode.ByDate
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .padding(top = 4.dp),
        bottomBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {

                    },
                    text = { Text("По предметам") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { /* Will be handled by pager */ },
                    text = { Text("По датам") }
                )
            }
        },
        topBar = {
            UserInfoTopBar(
                personName = uiState.personName,
                role = uiState.personRole,
                icons = {
                    PeriodsIcon(
                        "1 полугодие"
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(top = 4.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> SubjectMarksList(marks = uiState.marksBySubject)
                        1 -> DateMarksList(marksByDate = uiState.marksByDate)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectMarksList(marks: List<LessonMarks>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(marks) { lesson ->
            SubjectMarksCard(lesson = lesson)
        }
    }
}

@Composable
private fun DateMarksList(marksByDate: Map<String, List<Mark>>) {
    val sortedDates = remember(marksByDate) {
        marksByDate.keys.sortedDescending()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedDates) { date ->
            DateMarksCard(
                date = date,
                marks = marksByDate[date] ?: emptyList()
            )
        }
    }
}

@Composable
private fun SubjectMarksCard(lesson: LessonMarks) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lesson.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(4.dp))

                lesson.average?.let { avg ->
                    AverageScoreBadge(average = avg)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            MarksGrid(marks = lesson.marks)
        }
    }
}

@Composable
private fun AverageScoreBadge(average: String) {
    val borderColor = when {
        average.toFloat() < 2.5 -> MaterialTheme.colorScheme.error
        average.toFloat() < 3.5 -> Color(0xFFFFA500)
        average.toFloat() < 4.5 -> Color(0xFFFFFF00)
        else -> Color(53, 208, 10, 255)
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Text(
            text = average,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MarksGrid(marks: List<Mark>) {
    val marksPerRow = 4
    val rows = marks.chunked(marksPerRow)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        rows.forEach { rowMarks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowMarks.forEach { mark ->
                    MarkCard(
                        mark = mark,
                        modifier = Modifier.size(65.dp)
                    )
                }

                repeat(marksPerRow - rowMarks.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MarkCard(
    mark: Mark,
    modifier: Modifier = Modifier
) {
    val cardColor = when (mark.value) {
        "2" -> MaterialTheme.colorScheme.error
        "3" -> Color(0xFFFFA500).copy(alpha = 0.9f)
        "4" -> Color(89, 190, 1, 255).copy(alpha = 0.9f)
        "5" -> Color(38, 182, 0, 255).copy(alpha = 0.9f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mark.value,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.surface,
            )

            if (mark.weightFloat != null) {
                Text(
                    text = "x${mark.weightFloat}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            
            /*mark.mtype?.let { type ->
                Text(
                    text = type.short,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (mark.comment != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mark.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }*/
        }
    }
}

@Composable
private fun DateMarksCard(date: String, marks: List<Mark>) {
    val formattedDate = remember(date) {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .format(DateTimeFormatter.ofPattern("d MMMM"))
    }

    val daysBeforeThis = remember(date) {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ChronoUnit.DAYS.between(parsedDate, LocalDate.now()).toInt()
    }

    val daysAfterThisText = when (daysBeforeThis) {
        0 -> {
            "Сегодня"
        }
        1 -> {
            "Вчера"
        }
        else -> {
            "$daysBeforeThis дней(-я) назад"
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = daysAfterThisText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            marks.forEach { mark ->
                MarkInfo(mark = mark)

                if (mark != marks.last()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MarkInfo(mark: Mark) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mark.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    text = "x${mark.weightFloat}" ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Right
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = mark.lessonComment ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                
                if (mark.mtype != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mark.mtype.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}
