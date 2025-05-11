package com.team.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.team.common.components.icons.BellIcon
import com.team.common.components.icons.SettingsIcon

@Composable
fun UserInfoTopBar(
    personName: String = "",
    role: String = "student",
    onBellClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    icons: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 6.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clickable {

                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatarCircle(
                title = personName
            )

            UsernameAndRole(
                personName = personName,
                personRole = role
            )
        }

        icons()
        /*Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BellIcon(
                onBellClick = {
                    onBellClick()
                }
            )

            SettingsIcon(
                onSettingsClick = {
                    onSettingsClick()
                }
            )
        }*/
    }

}

@Composable
fun UsernameAndRole(
    personName: String,
    personRole: String
) {
    val roleText = when (personRole) {
        "student" -> "Ученик"
        else -> "Родитель"
    }

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = personName,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = roleText,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray
        )
    }
}

