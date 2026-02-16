package com.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.core.ui.viewmodel.TeamGamesViewModel

@Composable
fun TeamGamesBottomSheetContainer(
    teamId: Int,
    teamName: String,
    onDismiss: () -> Unit,
    viewModel: TeamGamesViewModel = hiltViewModel()
) {
    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    
    LaunchedEffect(teamId) {
        viewModel.loadGames(teamId)
    }
    
    TeamGamesBottomSheet(
        teamName = teamName,
        games = games,
        isLoading = isLoading,
        hasMorePages = hasMorePages,
        onLoadMore = { viewModel.loadMoreGames() },
        onDismiss = onDismiss
    )
}