package com.core.domain.model

data class Player(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val position: String,
    val height: Int?,
    val weight: Int?,
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