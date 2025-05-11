package com.team.common.components.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team.common.R

@Composable
fun SettingsIcon(
    onSettingsClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = R.drawable.settings_24px),
        contentDescription = null,
        modifier = Modifier
            .size(28.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onSettingsClick()
                }
            )
    )
}