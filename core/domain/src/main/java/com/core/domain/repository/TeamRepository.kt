package com.core.domain.repository

import com.core.domain.model.Team

interface TeamRepository {
    suspend fun getTeams(): Result<List<Team>>
}