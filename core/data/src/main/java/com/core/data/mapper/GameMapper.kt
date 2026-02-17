package com.core.data.mapper

import com.core.domain.model.Game
import com.core.domain.model.GamePage
import com.core.network.dto.GameDto
import com.core.network.dto.GameResponseDto

fun GameDto.toDomain(): Game {
    return Game(
        id = id,
        date = date,
        homeTeam = homeTeam.toDomain(),
        homeTeamScore = homeTeamScore,
        visitorTeam = visitorTeam.toDomain(),
        visitorTeamScore = visitorTeamScore,
        season = season,
        period = period,
        status = status,
        time = time
    )
}

fun GameResponseDto.toDomain(): GamePage {
    return GamePage(
        games = data.map { it.toDomain() },
        nextCursor = meta.nextCursor
    )
}