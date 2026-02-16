package com.features.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.core.domain.model.Team
import com.core.ui.components.ErrorState
import com.core.ui.components.LoadingState
import com.core.ui.components.SortDialog
import com.features.home.R
import com.features.home.viewmodel.HomeUiState
import com.features.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onTeamClick: (Int, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    var showSortDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.tertiary)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            ),
            title = {
                Text(
                    text = stringResource(id = R.string.home_top_app_bar_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            },
            actions = {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showSortDialog = true }
                        .padding(vertical = 4.dp, horizontal = 16.dp),
                    text = sortType.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                )
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingState()
                }

                is HomeUiState.Success -> {
                    TeamsList(
                        teams = state.teams,
                        onTeamClick = onTeamClick
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }

        if (showSortDialog) {
            SortDialog(
                currentSortType = sortType,
                onSortSelected = viewModel::onSortSelected,
                onDismiss = { showSortDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TeamsList(
    teams: List<Team>,
    onTeamClick: (Int, String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_label_name),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = stringResource(R.string.home_label_city),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = stringResource(R.string.home_label_conference),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teams) { team ->
                TeamItem(
                    team = team,
                    onClick = { onTeamClick(team.id, team.fullName) }
                )
            }
        }
    }
}

@Composable
private fun TeamItem(
    team: Team,
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
            Text(
                text = team.fullName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f)
            )

            Text(
                text = team.city,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.6f)
            )

            Text(
                text = team.conference,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.6f)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View games",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}