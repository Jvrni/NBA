package com.core.domain.usecase

import com.core.domain.model.Team
import com.core.domain.repository.TeamRepository
import com.core.domain.result.AppResult
import javax.inject.Inject


class GetTeams @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(): AppResult<List<Team>> {
        return teamRepository.getTeams()
    }
}