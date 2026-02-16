package com.core.ui

import com.core.domain.model.Game
import com.core.domain.model.GamePage
import com.core.domain.result.AppResult
import com.core.domain.usecase.GetTeamGames
import com.core.ui.viewmodel.TeamGamesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        useCase = mockk()
        viewModel = TeamGamesViewModel(useCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state has empty games list`() {
        assertTrue(viewModel.games.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasMorePages.value)
    }
    
    @Test
    fun `loadGames returns success with games`() = runTest {
        // Given
        val mockGames = listOf(
            mockk<Game>(relaxed = true),
            mockk<Game>(relaxed = true)
        )
        val mockGamePage = GamePage(
            games = mockGames,
            hasNextPage = true
        )
        coEvery { useCase(1, 1) } returns AppResult.Success(mockGamePage)
        
        // When
        viewModel.loadGames(1)
        advanceUntilIdle()
        
        // Then
        assertEquals(2, viewModel.games.value.size)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.hasMorePages.value)
        
        coVerify(exactly = 1) { useCase(1, 1) }
    }
    
    @Test
    fun `loadMoreGames appends games to existing list`() = runTest {
        // Given
        val firstPageGames = listOf(mockk<Game>(relaxed = true))
        val firstPageResult = GamePage(firstPageGames, true)
        coEvery { useCase(1, 1) } returns AppResult.Success(firstPageResult)
        
        viewModel.loadGames(1)
        advanceUntilIdle()
        
        // Given
        val secondPageGames = listOf(mockk<Game>(relaxed = true))
        val secondPageResult = GamePage(secondPageGames, true)
        coEvery { useCase(1, 2) } returns AppResult.Success(secondPageResult)
        
        // When
        viewModel.loadMoreGames()
        advanceUntilIdle()
        
        // Then
        assertEquals(2, viewModel.games.value.size)
        assertTrue(viewModel.hasMorePages.value)
        
        coVerify(exactly = 1) { useCase(1, 1) }
        coVerify(exactly = 1) { useCase(1, 2) }
    }
    
    @Test
    fun `loadGames resets state for new team`() = runTest {
        // Given
        val team1Games = listOf(mockk<Game>(relaxed = true))
        coEvery { useCase(1, 1) } returns AppResult.Success(
            GamePage(team1Games, false)
        )
        viewModel.loadGames(1)
        advanceUntilIdle()
        
        // When
        val team2Games = listOf(
            mockk<Game>(relaxed = true),
            mockk<Game>(relaxed = true)
        )
        coEvery { useCase(2, 1) } returns AppResult.Success(
            GamePage(team2Games, false)
        )
        viewModel.loadGames(2)
        advanceUntilIdle()
        
        // Then
        assertEquals(2, viewModel.games.value.size)
    }
    
    @Test
    fun `loadMoreGames does nothing when already loading`() = runTest {
        // Given
        val mockGamePage = GamePage(
            games = listOf(mockk(relaxed = true)),
            hasNextPage = true
        )
        
        coEvery { useCase(1, any()) } coAnswers {
            kotlinx.coroutines.delay(1000)
            AppResult.Success(mockGamePage)
        }
        
        viewModel.loadGames(1)
        
        // When
        viewModel.loadMoreGames()
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 1) { useCase(1, 1) }
        coVerify(exactly = 0) { useCase(1, 2) }
    }
    
    @Test
    fun `loadMoreGames does nothing when no more pages`() = runTest {
        // Given
        val mockGamePage = GamePage(
            games = listOf(mockk(relaxed = true)),
            hasNextPage = false
        )
        coEvery { useCase(1, 1) } returns AppResult.Success(mockGamePage)
        
        viewModel.loadGames(1)
        advanceUntilIdle()
        
        // When
        viewModel.loadMoreGames()
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 1) { useCase(1, 1) }
        coVerify(exactly = 0) { useCase(1, 2) }
    }
    
    @Test
    fun `loadGames handles error`() = runTest {
        // Given
        coEvery { useCase(1, 1) } returns AppResult.Error("Network error")
        
        // When
        viewModel.loadGames(1)
        advanceUntilIdle()
        
        // Then
        assertTrue(viewModel.games.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.hasMorePages.value)
    }
}