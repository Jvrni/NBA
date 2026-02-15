package com.core.domain.repository

import com.core.domain.model.Team
import com.core.domain.result.AppResult

interface TeamRepository {
    suspend fun getTeams(): AppResult<List<Team>>
}