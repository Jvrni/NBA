package com.core.domain

import com.core.domain.model.Team
import com.core.domain.repository.TeamRepository
import com.core.domain.result.AppResult
import com.core.domain.result.getOrNull
import com.core.domain.usecase.GetTeams
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetTeamsTest {
    
    private lateinit var teamRepository: TeamRepository
    private lateinit var useCase: GetTeams
    
    @Before
    fun setup() {
        teamRepository = mockk()
        useCase = GetTeams(teamRepository)
    }
    
    @Test
    fun `invoke calls repository and returns success`() = runTest {
        // Given
        val mockTeams = listOf(
            Team(
                id = 1,
                abbreviation = "ATL",
                city = "Atlanta",
                conference = "East",
                division = "Southeast",
                fullName = "Atlanta Hawks",
                name = "Hawks"
            )
        )
        coEvery { teamRepository.getTeams() } returns AppResult.Success(mockTeams)

        // When
        val result = useCase()

        // Then
        assertTrue(result is AppResult.Success)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 1) { teamRepository.getTeams() }
    }
    
    @Test
    fun `invoke returns failure on repository error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { teamRepository.getTeams() } returns AppResult.Error(errorMessage)

        // When
        val result = useCase()

        // Then
        assertTrue(result is AppResult.Error)
        assertEquals(errorMessage, (result as AppResult.Error).message)
    }
}