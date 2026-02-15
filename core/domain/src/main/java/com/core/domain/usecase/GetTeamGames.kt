package com.core.domain.usecase

import com.core.domain.model.GamePage
import com.core.domain.repository.GameRepository
import javax.inject.Inject

class GetTeamGames @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(teamId: Int, page: Int = 1): Result<GamePage> {
        return gameRepository.getTeamGames(teamId, page)
    }
}