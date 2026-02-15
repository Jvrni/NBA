package com.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerResponseDto(
    @field:Json(name = "data")
    val data: List<PlayerDto>,

    @field:Json(name = "meta")
    val meta: MetaDto
)

@JsonClass(generateAdapter = true)
data class PlayerDto(
    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "first_name")
    val firstName: String,

    @field:Json(name = "last_name")
    val lastName: String,

    @field:Json(name = "position")
    val position: String,

    @field:Json(name = "height")
    val height: Int?,

    @field:Json(name = "weight")
    val weight: Int?,

    @field:Json(name = "team")
    val team: TeamDto
)