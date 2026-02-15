package com.core.domain.model

data class Player(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val position: String,
    val heightFeet: Int?,
    val heightInches: Int?,
    val weightPounds: Int?,
    val team: Team
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class PlayerPage(
    val players: List<Player>,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean
)