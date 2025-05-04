package com.team.feature_diary.presentation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.feature_diary.presentation.components.UserAvatarCircle
import com.team.feature_diary.presentation.components.UserAvatarCirclePreview
import com.team.feature_diary.presentation.state.DiaryState

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

    if (!state.isLoading) {
        DiaryScreenContent(state)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(28.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DiaryScreenContent(
    state: DiaryState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CustomTopAppBar(
            name = state.studentInfo?.name ?: "None",

        )
    }
}

@Composable
fun CustomTopAppBar(name: String) {
    UserAvatarCircle(name, 40)
}

