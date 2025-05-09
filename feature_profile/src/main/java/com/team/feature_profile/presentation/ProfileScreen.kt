package com.team.feature_profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit
) {
    ProfileScreenContent(
        onBackClick = {
            onBackClick()
        }
    )
}

@Composable
private fun ProfileScreenContent(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        TopProfileBar(
            "Mike Potanin",
            "Student"
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null
        )
    }
}

@Composable
fun TopProfileBar(
    name: String,
    role: String
) {

}

@Preview
@Composable
private fun ProfileScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        //ProfileScreenContent()
    }
}

@Preview
@Composable
private fun ProfileScreenLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        //ProfileScreenContent()
    }
}

