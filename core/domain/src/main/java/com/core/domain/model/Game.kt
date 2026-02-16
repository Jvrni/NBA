package com.core.domain.model

data class Game(
    val id: Int,
    val date: String,
    val homeTeam: Team,
    val homeTeamScore: Int,
    val visitorTeam: Team,
    val visitorTeamScore: Int,
    val season: Int,
    val period: Int,
    val status: String,
    val time: String?
)

data class GamePage(
    val games: List<Game>,
    val hasNextPage: Boolean
)