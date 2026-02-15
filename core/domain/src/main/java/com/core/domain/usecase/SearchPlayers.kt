package com.core.domain.usecase

import com.core.domain.model.PlayerPage
import com.core.domain.repository.PlayerRepository
import com.core.domain.result.AppResult
import javax.inject.Inject

class SearchPlayers @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): AppResult<PlayerPage> {
        if (query.isBlank()) {
            return AppResult.Error("Search query cannot be empty")
        }
        
        return playerRepository.searchPlayers(query.trim(), page)
    }
}