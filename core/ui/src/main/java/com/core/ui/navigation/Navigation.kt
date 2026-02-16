package com.core.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Navigation

@Serializable
object Home: Navigation

@Serializable
object Players: Navigation