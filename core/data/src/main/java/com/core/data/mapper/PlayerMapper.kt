package com.core.data.mapper

import com.core.domain.model.Player
import com.core.domain.model.PlayerPage
import com.core.network.dto.PlayerDto
import com.core.network.dto.PlayerResponseDto

fun PlayerDto.toDomain(): Player {
    return Player(
        id = id,
        firstName = firstName,
        lastName = lastName,
        position = position,
        height = height,
        weight = weight,
        team = team.toDomain()
    )
}

fun PlayerResponseDto.toDomain(): PlayerPage {
    return PlayerPage(
        players = data.map { it.toDomain() },
        hasNextPage = meta.nextPage != null
    )
}