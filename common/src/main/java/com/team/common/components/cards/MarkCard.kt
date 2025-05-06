package com.team.common.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MarkCard(
    grade: Int = 0,
    weight: Float = 1f,
    comment: String? = null,
) {
    val borderColor = when (grade) {
        0 -> MaterialTheme.colorScheme.onSurface
        1 -> Color(255, 0, 0, 255)
        2 -> Color(255, 0, 0, 255)
        3 -> Color(255, 140, 0, 255)
        4 -> Color(215, 222, 0, 255)
        5 -> Color(0, 159, 3, 255)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = grade.toString(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                modifier = Modifier,
                text = "x${weight}",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun MarkCardPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        MarkCard()
    }
}