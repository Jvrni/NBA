package com.core.data.repository

import com.core.data.mapper.toDomain
import com.core.data.util.safeApiCall
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.model.Team
import com.core.domain.repository.TeamRepository
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val apiService: NbaApiService,
    private val dispatcherProvider: DispatcherProvider
) : TeamRepository {
    
    override suspend fun getTeams(): AppResult<List<Team>> = withContext(dispatcherProvider.io) {
        safeApiCall {
            val response = apiService.getTeams()
            response.data.toDomain()
        }
    }
}