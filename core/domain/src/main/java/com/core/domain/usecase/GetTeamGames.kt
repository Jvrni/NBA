package com.core.domain.usecase

import com.core.domain.model.GamePage
import com.core.domain.repository.GameRepository
import com.core.domain.result.AppResult
import javax.inject.Inject

class GetTeamGames @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(teamId: Int, page: Int = 1): AppResult<GamePage> {
        return gameRepository.getTeamGames(teamId, page)
    }
}