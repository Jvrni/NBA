package com.core.data.mapper

import com.core.domain.model.Team
import com.core.network.dto.TeamDto

fun TeamDto.toDomain(): Team {
    return Team(
        id = id,
        abbreviation = abbreviation,
        city = city,
        conference = conference,
        division = division,
        fullName = fullName,
        name = name
    )
}

fun List<TeamDto>.toDomain(): List<Team> {
    return map { it.toDomain() }
}