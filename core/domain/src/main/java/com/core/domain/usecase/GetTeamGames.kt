package com.core.domain.usecase

import androidx.paging.PagingData
import com.core.domain.model.Game
import com.core.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTeamGames @Inject constructor(
    private val gameRepository: GameRepository
) {
    operator fun invoke(teamId: Int, page: Int = 1): Flow<PagingData<Game>> {
        return gameRepository.getTeamGames(teamId, page)
    }
}