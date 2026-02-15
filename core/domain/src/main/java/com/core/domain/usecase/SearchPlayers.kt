package com.core.domain.usecase

import com.core.domain.model.PlayerPage
import com.core.domain.repository.PlayerRepository
import javax.inject.Inject

class SearchPlayers @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<PlayerPage> {
        if (query.isBlank()) {
            return Result.failure(IllegalArgumentException("Search query cannot be empty"))
        }
        
        return playerRepository.searchPlayers(query.trim(), page)
    }
}