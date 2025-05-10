package com.team.niceeljur.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team.feature_diary.presentation.screens.DiaryScreen
import com.team.feature_homework.presentation.HomeworkScreen
import com.team.feature_login.data.model.TokenResult
import com.team.feature_login.presentation.LoginScreen
import com.team.feature_marks.presentation.MarksScreen
import com.team.feature_messages.presentation.MessagesScreen
import com.team.feature_profile.presentation.ProfileScreen
import com.team.niceeljur.R
import com.team.niceeljur.navigation.RootNavDestinations.Diary
import com.team.niceeljur.navigation.RootNavDestinations.FinalGrades
import com.team.niceeljur.navigation.RootNavDestinations.Marks
import com.team.niceeljur.navigation.RootNavDestinations.Messages
import com.team.niceeljur.navigation.RootNavDestinations.More
import com.team.niceeljur.navigation.bottomNavigation.BottomNavBar
import com.team.niceeljur.navigation.bottomNavigation.BottomNavItem
import com.team.niceeljur.navigation.components.MoreBottomSheet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavigation() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val rootNavController = rememberNavController()

    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val sharedPrefs = context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
    val jwtToken = sharedPrefs.getString("jwt_token", "")
    val jwtTokenExpires = sharedPrefs.getString("jwt_token_expires", "")

    val startDestination = if (jwtToken.isNullOrEmpty() || checkTokenExpired(jwtTokenExpires)) {
        RootNavDestinations.Login.route
    } else {
        Diary.route
    }

    val bottomNavDestinations = listOf(
        Diary.route,
        Marks.route,
        RootNavDestinations.Homework.route,
        Messages.route,
        More.route,
        FinalGrades.route
    )

    if (showBottomSheet) {
        MoreBottomSheet(
            sheetState = sheetState,
            onDismiss = { showBottomSheet = false },
            navController = rootNavController
        )
    }

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomNavDestinations) {
                BottomNavBar(
                    navController = rootNavController,
                    destinations = listOf(
                        BottomNavItem(
                            route = Diary.route,
                            labelRes = R.string.diary,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.home_24px_filled),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.home_24px_not_filled)
                        ),
                        BottomNavItem(
                            route = Marks.route,
                            labelRes = R.string.marks,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.star_24px_filled),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.star_24px_not_filled)
                        ),
                        BottomNavItem(
                            route = RootNavDestinations.Homework.route,
                            labelRes = R.string.homework,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.school_24px_filled),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.school_24px_not_filled)
                        ),
                        BottomNavItem(
                            route = Messages.route,
                            labelRes = R.string.messages,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.forum_24px_filled),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.forum_24px_not_filled)
                        ),
                        BottomNavItem(
                            route = More.route,
                            labelRes = R.string.more,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.menu_24px),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.menu_24px)
                        )
                    ),
                    onMoreClick = {
                        scope.launch {
                            showBottomSheet = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
            navController = rootNavController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(RootNavDestinations.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        rootNavController.navigate(Diary.route) {
                            popUpTo(RootNavDestinations.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Diary.route) {
                DiaryScreen(
                    onProfileIconClick = {
                        rootNavController.navigate(RootNavDestinations.Profile.route)
                    }
                )
            }

            composable(RootNavDestinations.Profile.route) {
                ProfileScreen(
                    onBackClick = {
                        rootNavController.popBackStack()
                    }
                )
            }

            composable(Marks.route) {
                MarksScreen()
            }

            composable(Messages.route) {
                MessagesScreen()
            }

            composable(FinalGrades.route) {
                // FinalGradesScreen(navController = rootNavController)
            }

            composable(RootNavDestinations.Homework.route) {
                HomeworkScreen()
            }
        }
    }
}

fun checkTokenExpired(jwtTokenExpires: String?): Boolean {
    if (jwtTokenExpires.isNullOrEmpty()) return true

    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val expirationDate = formatter.parse(jwtTokenExpires)
        val currentDate = Date()
        expirationDate?.before(currentDate) ?: true
    } catch (e: Exception) {
        true
    }
}
