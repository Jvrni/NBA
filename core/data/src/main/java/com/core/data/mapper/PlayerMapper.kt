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
        height = height?.toHeightInCm(),
        weight = weight?.toWeightInKg(),
        team = team.toDomain()
    )
}

private fun String.toHeightInCm(): Int? {
    return try {
        val parts = this.split("-")
        if (parts.size == 2) {
            val feet = parts[0].toIntOrNull() ?: return null
            val inches = parts[1].toIntOrNull() ?: return null
            val totalInches = (feet * 12) + inches
            (totalInches * 2.54).toInt()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

private fun String.toWeightInKg(): Int? {
    return try {
        val pounds = this.toIntOrNull() ?: return null
        (pounds * 0.453592).toInt()
    } catch (e: Exception) {
        null
    }
}

fun PlayerResponseDto.toDomain(): PlayerPage {
    return PlayerPage(
        players = data.map { it.toDomain() },
        nextCursor = meta.nextCursor
    )
}