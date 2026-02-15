package com.core.domain.repository

import com.core.domain.model.GamePage
import com.core.domain.result.AppResult

interface GameRepository {
    suspend fun getTeamGames(teamId: Int, page: Int): AppResult<GamePage>
}