package com.core.domain.repository

import com.core.domain.model.PlayerPage

interface PlayerRepository {
    suspend fun searchPlayers(query: String, page: Int): Result<PlayerPage>
}