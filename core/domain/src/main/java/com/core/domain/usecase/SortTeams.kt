package com.core.domain.usecase

import com.core.domain.model.SortType
import com.core.domain.model.Team
import javax.inject.Inject
import kotlin.collections.sortedBy

class SortTeams @Inject constructor() {
    
    operator fun invoke(teams: List<Team>, sortBy: SortType): List<Team> {
        return when (sortBy) {
            SortType.NAME -> teams.sortedBy { it.fullName }
            SortType.CITY -> teams.sortedBy { it.city }
            SortType.CONFERENCE -> teams.sortedBy { it.conference }
        }
    }
}