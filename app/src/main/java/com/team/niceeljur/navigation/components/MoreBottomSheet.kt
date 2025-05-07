package com.team.niceeljur.navigation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team.niceeljur.R
import com.team.niceeljur.navigation.RootNavDestinations
import com.team.niceeljur.navigation.bottomNavigation.MoreBottomSheetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val destinations = listOf(
        MoreBottomSheetItem(
            name = R.string.final_grades,
            route = RootNavDestinations.FinalGrades.route,
            icon = R.drawable.star_24px_filled
        ),
        MoreBottomSheetItem(
            name = R.string.announcements,
            route = RootNavDestinations.Announcements.route,
            icon = R.drawable.campaign_24px
        ),
        MoreBottomSheetItem(
            name = R.string.rating,
            route = RootNavDestinations.Rating.route,
            icon = R.drawable.leaderboard_24px
        ),
        MoreBottomSheetItem(
            name = R.string.settings,
            route = RootNavDestinations.Settings.route,
            icon = R.drawable.settings_24px
        ),
        MoreBottomSheetItem(
            name = R.string.about_us,
            route = RootNavDestinations.About.route,
            icon = R.drawable.info_24px
        )
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            destinations.forEach { destination ->
                MoreItem(
                    icon = ImageVector.vectorResource(id = destination.icon),
                    title = stringResource(destination.name),
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun MoreItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(start = 24.dp, end = 16.dp)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
} 