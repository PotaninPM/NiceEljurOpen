package com.team.common.components.icons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team.common.R


@Composable
fun FilterIcon(
    onFilterClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier,
        onClick = {
            onFilterClick()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tune_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(26.dp)
            )
        }

    }
}

