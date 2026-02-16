package com.core.domain.repository

import androidx.paging.PagingData
import com.core.domain.model.Game
import com.core.domain.model.GamePage
import com.core.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getTeamGames(teamId: Int, page: Int): Flow<PagingData<Game>>
}