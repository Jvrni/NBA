package com.nba.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.core.domain.model.SelectedTeam
import com.core.ui.components.TeamGamesBottomSheetContainer
import com.core.ui.model.Menu
import com.core.ui.navigation.Home
import com.features.home.graph.homeGraph
import kotlin.collections.forEach

@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val menu = Menu.entries.toTypedArray()
    val selected = remember { mutableStateOf(Menu.Home) }


    val selectedTeam = remember { mutableStateOf<SelectedTeam?>(null) }

    Scaffold(
        bottomBar = {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.2f)
                    )

                    NavigationBar(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ) {
                        menu.forEach { item ->
                            NavigationBarItem(
                                selected = selected.value == item,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (selected.value == item) FontWeight.Bold
                                            else FontWeight.Normal
                                        ),
                                    )
                                },
                                onClick = {
                                    selected.value = item
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            homeGraph {
                selectedTeam.value = it
            }
        }

        selectedTeam.value?.let { team ->
            TeamGamesBottomSheetContainer(
                teamId = team.id,
                teamName = team.name,
                onDismiss = { selectedTeam.value = null }
            )
        }
    }
}