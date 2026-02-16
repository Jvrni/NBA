package com.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.core.data.mapper.toDomain
import com.core.data.paging.GenericPagingSource
import com.core.data.util.safeApiCall
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.repository.PlayerRepository
import com.core.network.api.NbaApiService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val apiService: NbaApiService,
    private val dispatcherProvider: DispatcherProvider,
) : PlayerRepository {
    
    override fun searchPlayers(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false,
                initialLoadSize = 25,
                prefetchDistance = 5,
                maxSize = 200
            ),
            pagingSourceFactory = {
                GenericPagingSource { page ->
                    withContext(dispatcherProvider.io) {
                        safeApiCall {
                            val response = apiService.searchPlayers(
                                playerName = query,
                                page = page
                            )

                            response.toDomain()
                        }
                    }
                }
            }
        ).flow
}