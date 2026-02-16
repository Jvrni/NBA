package com.core.ui

import androidx.paging.PagingData
import app.cash.turbine.test
import com.core.domain.model.Game
import com.core.domain.usecase.GetTeamGames
import com.core.ui.viewmodel.TeamGamesViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamGamesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var useCase: GetTeamGames
    private lateinit var viewModel: TeamGamesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk(relaxed = true)
        viewModel = TeamGamesViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGames triggers use case with teamId`() = runTest {
        // Given
        val teamId = 1
        val mockGames = listOf(
            mockk<Game>(relaxed = true),
            mockk<Game>(relaxed = true)
        )
        val pagingData = PagingData.from(mockGames)

        every { useCase(teamId, any()) } returns flowOf(pagingData)

        // When
        viewModel.loadGames(teamId)

        // Collect from the flow to trigger the use case
        viewModel.gamesPagingData.test {
            awaitItem() // Wait for first emission
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify { useCase(teamId, any()) }
    }

    @Test
    fun `loadGames switches teamId correctly`() = runTest {
        // Given
        val teamId1 = 1
        val teamId2 = 2
        val mockGames = listOf(mockk<Game>(relaxed = true))
        val pagingData = PagingData.from(mockGames)

        every { useCase(any(), any()) } returns flowOf(pagingData)

        // When - First team
        viewModel.loadGames(teamId1)

        // Collect to trigger the use case
        viewModel.gamesPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify { useCase(teamId1, any()) }

        // When - Switch to second team
        viewModel.loadGames(teamId2)

        // Collect again to trigger the use case with new teamId
        viewModel.gamesPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify { useCase(teamId2, any()) }
    }

    @Test
    fun `gamesPagingData with null teamId returns empty`() = runTest {
        // Given - ViewModel initialized with null teamId

        // When
        viewModel.gamesPagingData.test {
            val firstEmission = awaitItem()

            // Then - Should emit empty PagingData
            assertNotNull(firstEmission)
            cancelAndIgnoreRemainingEvents()
        }

        // Use case não deve ser chamado com teamId null
        verify(exactly = 0) { useCase(any()) }
    }
}
