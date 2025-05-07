package com.team.niceeljur.navigation.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MoreBottomSheetItem(
    @StringRes val name: Int,
    val route: String,
    @DrawableRes val icon: Int
)

