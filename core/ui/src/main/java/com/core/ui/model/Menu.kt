package com.core.ui.model

import com.core.ui.navigation.Navigation

enum class Menu(val title: String, val route: Navigation) {
    Home(title = "Home", route = com.core.ui.navigation.Home),
    Players(title = "Players", route = com.core.ui.navigation.Players)
}