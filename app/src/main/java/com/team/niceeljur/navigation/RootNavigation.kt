package com.team.niceeljur.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team.feature_login.presentation.LoginScreen
import com.team.niceeljur.R
import com.team.niceeljur.navigation.RootNavDestinations.Diary
import com.team.niceeljur.navigation.RootNavDestinations.FinalGrades
import com.team.niceeljur.navigation.RootNavDestinations.Marks
import com.team.niceeljur.navigation.RootNavDestinations.Messages
import com.team.niceeljur.navigation.bottomNavigation.BottomNavBar
import com.team.niceeljur.navigation.bottomNavigation.BottomNavItem

@Composable
fun RootNavigation() {
    val context = LocalContext.current

    val rootNavController = rememberNavController()

    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val sharedPrefs = context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
    val tokenExpirationTime = sharedPrefs.getString("token_expiration_time", "")

    /*val startDestination = if (tokenExpirationTime.isNullOrEmpty()) {
        RootNavDestinations.Login.route
    } else {
        Diary.route
    }*/
    val startDestination = RootNavDestinations.Login.route

    val bottomNavDestinations = listOf(
        RootNavDestinations.Homework.route,
        Diary.route,
        Marks.route,
        Messages.route,
        FinalGrades.route
    )

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
                            route = RootNavDestinations.More.route,
                            labelRes = R.string.more,
                            selectedIcon = ImageVector.vectorResource(id = R.drawable.menu_24px),
                            unselectedIcon = ImageVector.vectorResource(id = R.drawable.menu_24px)
                        )
                    )
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
                // DiaryScreen(navController = rootNavController)
            }

            composable(Marks.route) {
                // MarksScreen(navController = rootNavController)
            }

            composable(Messages.route) {
                // MessagesScreen(navController = rootNavController)
            }

            composable(FinalGrades.route) {
                // FinalGradesScreen(navController = rootNavController)
            }
        }
    }
}