package com.team.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    insideThing: (@Composable () -> Unit)? = null
) {
    val progressColor = Color(71, 141, 255, 255)

    val imageVector = if (progress == 1f) {
        Icons.Default.Check
    } else {
        Icons.Default.Check
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .size(55.dp)
        )

        if (progress == 1f) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color(71, 141, 255, 255)
            )
        } else {
            if (insideThing == null) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = progressColor,
                    )
                }
            } else {
                insideThing()
            }
        }
    }
}

