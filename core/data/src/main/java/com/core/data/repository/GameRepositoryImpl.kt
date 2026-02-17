package com.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.core.data.mapper.toDomain
import com.core.data.paging.GenericPagingSource
import com.core.data.util.safeApiCall
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.model.Game
import com.core.domain.model.GamePage
import com.core.domain.repository.GameRepository
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val apiService: NbaApiService,
    private val dispatcherProvider: DispatcherProvider
) : GameRepository {
    
    override fun getTeamGames(teamId: Int, page: Int): Flow<PagingData<Game>> =
        Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false,
                initialLoadSize = 25,
                prefetchDistance = 5,
                maxSize = 200
            ),
            pagingSourceFactory = {
                GenericPagingSource { cursor ->
                    withContext(dispatcherProvider.io) {
                        safeApiCall {
                            val response = apiService.getGames(teamId = teamId, cursor = cursor)
                            response.toDomain()
                        }
                    }
                }
            }
        ).flow
}