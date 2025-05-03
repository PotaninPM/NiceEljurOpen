package com.team.niceeljur.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RootNavDestinations(val route: String) {

    @Serializable
    data object Diary : RootNavDestinations("diary")

    @Serializable
    data object Marks : RootNavDestinations("marks")

    @Serializable
    data object Homework : RootNavDestinations("homework")

    @Serializable
    data object More : RootNavDestinations("more")

    @Serializable
    data object Login : RootNavDestinations("login")

    @Serializable
    data object Messages : RootNavDestinations("messages")

    @Serializable
    data object FinalGrades : RootNavDestinations("final_grades")
}