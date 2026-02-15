package com.core.domain.repository

import com.core.domain.model.PlayerPage
import com.core.domain.result.AppResult

interface PlayerRepository {
    suspend fun searchPlayers(query: String, page: Int): AppResult<PlayerPage>
}