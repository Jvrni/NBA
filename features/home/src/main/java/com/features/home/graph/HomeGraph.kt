package com.features.home.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.core.domain.model.SelectedTeam
import com.core.ui.navigation.Home
import com.features.home.ui.HomeScreen

fun NavGraphBuilder.homeGraph(onSelected: (SelectedTeam) -> Unit) {
    composable<Home> {
        HomeScreen(
            onTeamClick = { teamId, teamName ->
                onSelected.invoke(SelectedTeam(teamId, teamName))
            }
        )
    }
}