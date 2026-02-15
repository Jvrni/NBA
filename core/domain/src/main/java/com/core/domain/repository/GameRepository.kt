package com.core.domain.repository

import com.core.domain.model.GamePage

interface GameRepository {
    suspend fun getTeamGames(teamId: Int, page: Int): Result<GamePage>
}