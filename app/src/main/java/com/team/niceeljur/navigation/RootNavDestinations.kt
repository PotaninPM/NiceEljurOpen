package com.team.niceeljur.navigation

import kotlinx.serialization.Serializable

sealed class RootNavDestinations {

    @Serializable
    data object Diary : RootNavDestinations()

    @Serializable
    data object Marks : RootNavDestinations()

    @Serializable
    data object Login : RootNavDestinations()

    @Serializable
    data object Messages : RootNavDestinations()

    @Serializable
    data object FinalGrades : RootNavDestinations()
}