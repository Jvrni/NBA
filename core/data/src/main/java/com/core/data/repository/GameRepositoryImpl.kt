package com.core.data.repository

import com.core.data.mapper.toDomain
import com.core.data.util.safeApiCall
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.model.GamePage
import com.core.domain.repository.GameRepository
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val apiService: NbaApiService,
    private val dispatcherProvider: DispatcherProvider
) : GameRepository {
    
    override suspend fun getTeamGames(teamId: Int, page: Int): AppResult<GamePage> =
        withContext(dispatcherProvider.io) {
            safeApiCall {
                val response = apiService.getGames(teamId = teamId, page = page)
                response.toDomain()
            }
        }
}