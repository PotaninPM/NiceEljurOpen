package com.team.common.components.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team.common.R

@Composable
fun BellIcon(
    somethingNew: Boolean = true,
    onBellClick: () -> Unit
) {
    OutlinedCard {
        Column(
            modifier = Modifier
                .padding(7.dp)
        ) {
            BadgedBox(
                badge = {
                    if (somethingNew) {
                        Badge(
                            modifier = Modifier,
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.campaign_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                onBellClick()
                            }
                        )
                )
            }
        }
    }
}

