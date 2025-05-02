package com.team.niceeljur.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.team.feature_login.presentation.LoginScreen

@Composable
fun RootNavigation() {
    val context = LocalContext.current

    val rootNavController = rememberNavController()

    val sharedPrefs = context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
    val tokenExpirationTime = sharedPrefs.getString("token_expiration_time", "")

    val startDestination = if (tokenExpirationTime.isNullOrEmpty()) {
        RootNavDestinations.Login
    } else {
        RootNavDestinations.Diary
    }

    NavHost(
        startDestination = startDestination,
        navController = rootNavController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<RootNavDestinations.Login> {
            LoginScreen(
                navController = rootNavController,
                onLoginSuccess = {
                    //rootNavController.navigate(RootNavDestinations.Diary)
                }
            )
        }

        composable<RootNavDestinations.Diary> {
            // DiaryScreen(navController = rootNavController)
        }

        composable<RootNavDestinations.Marks> {
            // MarksScreen(navController = rootNavController)
        }

        composable<RootNavDestinations.Messages> {
            // MessagesScreen(navController = rootNavController)
        }

        composable<RootNavDestinations.FinalGrades> {
            // FinalGradesScreen(navController = rootNavController)
        }
    }
}