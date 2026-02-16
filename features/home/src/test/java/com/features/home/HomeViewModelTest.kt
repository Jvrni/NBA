package com.features.home

import app.cash.turbine.test
import com.core.domain.model.SortType
import com.core.domain.model.Team
import com.core.domain.result.AppResult
import com.core.domain.usecase.GetTeams
import com.core.domain.usecase.SortTeams
import com.features.home.viewmodel.HomeUiState
import com.features.home.viewmodel.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var getTeams: GetTeams
    private lateinit var sortTeams: SortTeams
    private val testDispatcher = StandardTestDispatcher()

    private val mockTeams = listOf(
        Team(
            id = 1,
            abbreviation = "LAL",
            city = "Los Angeles",
            conference = "West",
            division = "Pacific",
            fullName = "Los Angeles Lakers",
            name = "Lakers"
        ),
        Team(
            id = 2,
            abbreviation = "BOS",
            city = "Boston",
            conference = "East",
            division = "Atlantic",
            fullName = "Boston Celtics",
            name = "Celtics"
        ),
        Team(
            id = 3,
            abbreviation = "GSW",
            city = "Golden State",
            conference = "West",
            division = "Pacific",
            fullName = "Golden State Warriors",
            name = "Warriors"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTeams = mockk()
        sortTeams = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load teams successfully`() = runTest {
        // Given
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams

        // When
        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(mockTeams, (state as HomeUiState.Success).teams)
        }
        coVerify { getTeams() }
    }

    @Test
    fun `init should show error when getTeams fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getTeams() } returns AppResult.Error(errorMessage)

        // When
        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals(errorMessage, (state as HomeUiState.Error).message)
        }
    }

    @Test
    fun `loadTeams should set loading state before fetching`() = runTest {
        // Given
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams
        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.uiState.test {
            awaitItem() // Skip initial state
            viewModel.loadTeams()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is HomeUiState.Loading)

            val successState = awaitItem()
            assertTrue(successState is HomeUiState.Success)
        }
    }

    @Test
    fun `onSortSelected should update sort type and apply sorting by NAME`() = runTest {
        // Given
        val sortedByName = mockTeams.sortedBy { it.fullName }
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns sortedByName
        every { sortTeams(mockTeams, SortType.CITY) } returns mockTeams.sortedBy { it.city }

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.onSortSelected(SortType.CITY)
        advanceUntilIdle()

        // Then
        viewModel.sortType.test {
            assertEquals(SortType.CITY, awaitItem())
        }
    }

    @Test
    fun `onSortSelected should update sort type and apply sorting by CITY`() = runTest {
        // Given
        val sortedByCity = mockTeams.sortedBy { it.city }
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams
        every { sortTeams(mockTeams, SortType.CITY) } returns sortedByCity

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.onSortSelected(SortType.CITY)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(sortedByCity, (state as HomeUiState.Success).teams)
        }
    }

    @Test
    fun `onSortSelected should update sort type and apply sorting by CONFERENCE`() = runTest {
        // Given
        val sortedByConference = mockTeams.sortedBy { it.conference }
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams
        every { sortTeams(mockTeams, SortType.CONFERENCE) } returns sortedByConference

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.onSortSelected(SortType.CONFERENCE)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(sortedByConference, (state as HomeUiState.Success).teams)
        }
    }

    @Test
    fun `retry should call loadTeams again`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getTeams() } returns AppResult.Error(errorMessage) andThen AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getTeams() }
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
        }
    }

    @Test
    fun `retry should recover from error state to success state`() = runTest {
        // Given
        coEvery { getTeams() } returns AppResult.Error("Error") andThen AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // Verify error state
        viewModel.uiState.test {
            assertTrue(awaitItem() is HomeUiState.Error)
        }

        // When
        viewModel.uiState.test {
            awaitItem() // Skip current error state
            viewModel.retry()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is HomeUiState.Loading)

            val successState = awaitItem()
            assertTrue(successState is HomeUiState.Success)
            assertEquals(mockTeams, (successState as HomeUiState.Success).teams)
        }
    }

    @Test
    fun `default sortType should be NAME`() = runTest {
        // Given
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams

        // When
        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // Then
        viewModel.sortType.test {
            assertEquals(SortType.NAME, awaitItem())
        }
    }

    @Test
    fun `sorting should persist when teams are reloaded`() = runTest {
        // Given
        val sortedByCity = mockTeams.sortedBy { it.city }
        coEvery { getTeams() } returns AppResult.Success(mockTeams)
        every { sortTeams(mockTeams, SortType.NAME) } returns mockTeams
        every { sortTeams(mockTeams, SortType.CITY) } returns sortedByCity

        viewModel = HomeViewModel(getTeams, sortTeams)
        advanceUntilIdle()

        // When
        viewModel.onSortSelected(SortType.CITY)
        advanceUntilIdle()
        viewModel.loadTeams()
        advanceUntilIdle()

        // Then
        viewModel.sortType.test {
            assertEquals(SortType.CITY, awaitItem())
        }
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(sortedByCity, (state as HomeUiState.Success).teams)
        }
    }
}
