package com.features.players.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.core.domain.model.SelectedTeam
import com.core.ui.navigation.Players
import com.features.players.ui.PlayersScreen

fun NavGraphBuilder.playersGraph(onSelected: (SelectedTeam) -> Unit) {
    composable<Players> {
        PlayersScreen(
            onPlayerClick = { teamId, teamName ->
                onSelected.invoke(SelectedTeam(teamId, teamName))
            }
        )
    }
}
