package com.team.feature_marks.presentation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.feature_marks.data.model.LessonMarks
import com.team.feature_marks.data.model.Mark
import com.team.feature_marks.presentation.viewmodel.MarksDisplayMode
import com.team.feature_marks.presentation.viewmodel.MarksViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarksScreen(
    viewModel: MarksViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authToken = remember {
        context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
            .getString("jwt_token", "") ?: ""
    }

    val studentId = remember {
        context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
            .getString("student_id", "") ?: ""
    }

    Log.d("MarksScreen", "Auth token: $authToken Student ID: $studentId")

    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(Unit) {
        viewModel.loadMarks(authToken, studentId)
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
        topBar = {
            TopAppBar(
                title = { Text("Оценки") },
                actions = {
                    IconButton(onClick = { /* Refresh */ }) {
                        Icon(
                            if (uiState.displayMode == MarksDisplayMode.BySubject) 
                                Icons.Default.MailOutline
                            else 
                                Icons.Default.DateRange,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
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
                Column {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = { /* Will be handled by pager */ },
                            text = { Text("По предметам") },
                            icon = { Icon(Icons.Default.Add, null) }
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = { /* Will be handled by pager */ },
                            text = { Text("По датам") },
                            icon = { Icon(Icons.Default.DateRange, null) }
                        )
                    }

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
}

@Composable
private fun SubjectMarksList(marks: List<LessonMarks>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(marks) { lesson ->
            LessonMarksCard(lesson = lesson)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonMarksCard(lesson: LessonMarks) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lesson.name,
                    style = MaterialTheme.typography.titleMedium
                )
                lesson.average?.let { avg ->
                    Text(
                        text = "Средний балл: $avg",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lesson.marks.forEach { mark ->
                    MarkBadge(mark = mark)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateMarksCard(date: String, marks: List<Mark>) {
    val formattedDate = remember(date) {
        LocalDate.parse(date).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            marks.forEach { mark ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mark.lessonComment ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    MarkBadge(mark = mark)
                }
            }
        }
    }
}

@Composable
private fun MarkBadge(mark: Mark) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mark.value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            mark.mtype?.let { type ->
                Text(
                    text = type.short,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 