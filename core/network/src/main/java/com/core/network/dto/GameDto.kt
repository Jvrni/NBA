package com.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameResponseDto(
    @field:Json(name = "data")
    val data: List<GameDto>,

    @field:Json(name = "meta")
    val meta: MetaDto
)

@JsonClass(generateAdapter = true)
data class GameDto(
    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "date")
    val date: String,

    @field:Json(name = "home_team")
    val homeTeam: TeamDto,

    @field:Json(name = "home_team_score")
    val homeTeamScore: Int,

    @field:Json(name = "visitor_team")
    val visitorTeam: TeamDto,

    @field:Json(name = "visitor_team_score")
    val visitorTeamScore: Int,

    @field:Json(name = "season")
    val season: Int,

    @field:Json(name = "period")
    val period: Int,

    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "time")
    val time: String?
)

@JsonClass(generateAdapter = true)
data class MetaDto(

    @field:Json(name = "next_page")
    val nextPage: Int?,

    @field:Json(name = "per_page")
    val perPage: Int,

)