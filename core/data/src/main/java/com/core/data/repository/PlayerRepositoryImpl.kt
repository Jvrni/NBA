package com.core.data.repository

import com.core.data.mapper.toDomain
import com.core.data.util.safeApiCall
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.model.PlayerPage
import com.core.domain.repository.PlayerRepository
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: NbaApiService,
    private val dispatcherProvider: DispatcherProvider
) : PlayerRepository {
    
    override suspend fun searchPlayers(query: String, page: Int): AppResult<PlayerPage> =
        withContext(dispatcherProvider.io) {
            safeApiCall {
                val response = apiService.searchPlayers(playerName = query, page = page)
                response.toDomain()
            }
        }
}