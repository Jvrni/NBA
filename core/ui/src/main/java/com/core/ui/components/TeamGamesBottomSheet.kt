package com.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.core.domain.model.Game
import com.core.domain.model.Team
import com.core.ui.theme.NbaAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamGamesBottomSheet(
    teamName: String,
    games: List<Game>,
    isLoading: Boolean,
    hasMorePages: Boolean,
    onLoadMore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= games.size - 3 && hasMorePages && !isLoading
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss, contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Home", color = MaterialTheme.colorScheme.onTertiary)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = teamName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Home\nName",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Home\nScore",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Visitor\nName",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Visitor\nScore",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            if (games.isEmpty() && !isLoading) {
                EmptyState(message = "No games found")
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                ) {
                    items(games) { game ->
                        GameRow(game = game)
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    if (hasMorePages && !isLoading) {
                        item {
                            Text(
                                text = "...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameRow(game: Game) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = game.homeTeam.fullName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = game.homeTeamScore.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = game.visitorTeam.fullName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = game.visitorTeamScore.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun TeamGamesBottomSheetPreview() {
    NbaAppTheme {
        TeamGamesBottomSheet(
            teamName = "Los Angeles Lakers", games = listOf(
            Game(
                id = 1,
                date = "2023-04-01",
                homeTeam = Team(
                    id = 1,
                    abbreviation = "LAL",
                    city = "Los Angeles",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Los Angeles Lakers",
                    name = "Lakers"
                ),
                homeTeamScore = 100,

                visitorTeam = Team(
                    id = 2,
                    abbreviation = "GSW",
                    city = "Golden State",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Golden State Warriors",
                    name = "Warriors"
                ),
                visitorTeamScore = 98,
                status = "Final",
                time = "48:00",
                period = 4,
                season = 2023
            ), Game(
                id = 1,
                date = "2023-04-01",
                homeTeam = Team(
                    id = 1,
                    abbreviation = "LAL",
                    city = "Los Angeles",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Los Angeles Lakers",
                    name = "Lakers"
                ),
                homeTeamScore = 100,

                visitorTeam = Team(
                    id = 2,
                    abbreviation = "GSW",
                    city = "Golden State",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Golden State Warriors",
                    name = "Warriors"
                ),
                visitorTeamScore = 98,
                status = "Final",
                time = "48:00",
                period = 4,
                season = 2023
            ), Game(
                id = 1,
                date = "2023-04-01",
                homeTeam = Team(
                    id = 1,
                    abbreviation = "LAL",
                    city = "Los Angeles",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Los Angeles Lakers",
                    name = "Lakers"
                ),
                homeTeamScore = 100,

                visitorTeam = Team(
                    id = 2,
                    abbreviation = "GSW",
                    city = "Golden State",
                    conference = "West",
                    division = "Pacific",
                    fullName = "Golden State Warriors",
                    name = "Warriors"
                ),
                visitorTeamScore = 98,
                status = "Final",
                time = "48:00",
                period = 4,
                season = 2023
            )

        ), isLoading = false, hasMorePages = true, onLoadMore = {}, onDismiss = {})
    }
}