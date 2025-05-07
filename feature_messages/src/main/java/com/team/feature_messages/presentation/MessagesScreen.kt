package com.team.feature_messages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel(),
) {
    MessagesScreenContent()
}

@Composable
private fun MessagesScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

    }
}

@Preview
@Composable
private fun MessagesScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        MessagesScreenContent()
    }
}

@Preview
@Composable
private fun MessagesScreenLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        MessagesScreenContent()
    }
}

