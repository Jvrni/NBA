package com.core.domain.repository

import androidx.paging.PagingData
import com.core.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun searchPlayers(query: String): Flow<PagingData<Player>>
}