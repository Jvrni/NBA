package com.features.players.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.core.domain.model.Player
import com.core.ui.components.EmptyState
import com.core.ui.components.ErrorState
import com.core.ui.components.LoadingState
import com.features.players.R
import com.features.players.viewmodel.PlayersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    viewModel: PlayersViewModel = hiltViewModel(),
    onPlayerClick: (Int, String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val players = viewModel.playersPagingData.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            ),
            title = {
                Text(
                    text = stringResource(id = R.string.players_top_app_bar_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            isSearching = players.loadState.refresh is LoadState.Loading
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                players.loadState.refresh is LoadState.Loading && players.itemCount == 0 -> {
                    LoadingState()
                }

                players.loadState.refresh is LoadState.Error && players.itemCount == 0 -> {
                    val error = (players.loadState.refresh as LoadState.Error).error
                    ErrorState(
                        message = error.message ?: "Unknown error",
                        onRetry = { players.retry() }
                    )
                }

                players.itemCount == 0 && players.loadState.refresh !is LoadState.Loading -> {
                    EmptyState(message = stringResource(id = R.string.players_empty_result))
                }

                else -> {
                    PlayersList(
                        players = players,
                        onPlayerClick = onPlayerClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(id = R.string.players_search_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.players_search_icon_description)
            )
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun PlayersList(
    players: LazyPagingItems<Player>,
    onPlayerClick: (Int, String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.players_label_name),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = stringResource(id = R.string.players_label_position),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.players_label_team),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = players.itemCount,
                key = players.itemKey { it.id }
            ) { index ->
                players[index]?.let { player ->
                    PlayerItem(
                        player = player,
                        onClick = { onPlayerClick(player.team.id, player.team.fullName) }
                    )
                }
            }

            when (players.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        val error = (players.loadState.append as LoadState.Error).error
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(onClick = { players.retry() }) {
                                Text("Error: ${error.message}. Tap to retry")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun PlayerItem(
    player: Player,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = player.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                player.height?.let { height ->
                    player.weight?.let { weight ->
                        Text(
                            text = stringResource(id = R.string.players_height_weight_format, height, weight),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = player.position.ifEmpty { "-" },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = player.team.abbreviation,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.5f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(id = R.string.players_icon_view_games),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
