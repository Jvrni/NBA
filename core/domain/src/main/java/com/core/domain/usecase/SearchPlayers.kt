package com.core.domain.usecase

import androidx.paging.PagingData
import com.core.domain.model.Player
import com.core.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPlayers @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(query: String): Flow<PagingData<Player>> {
        return playerRepository.searchPlayers(query)
    }
}