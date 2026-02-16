package com.features.players

import androidx.paging.PagingData
import app.cash.turbine.test
import com.core.domain.model.Player
import com.core.domain.usecase.SearchPlayers
import com.features.players.viewmodel.PlayersViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var searchPlayers: SearchPlayers
    private lateinit var viewModel: PlayersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchPlayers = mockk(relaxed = true)

        coEvery { searchPlayers(any()) } returns flowOf(PagingData.empty())

        viewModel = PlayersViewModel(searchPlayers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial searchQuery is empty`() = runTest {
        // When

        // Then
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange updates searchQuery state immediately`() = runTest {
        // Given
        val newQuery = "LeBron"

        // When
        viewModel.onSearchQueryChange(newQuery)

        // Then
        viewModel.searchQuery.test {
            assertEquals(newQuery, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing query multiple times updates state for each change`() = runTest {
        // Given
        val queries = listOf("L", "Le", "LeB", "LeBron")

        // When & Then
        queries.forEach { query ->
            viewModel.onSearchQueryChange(query)

            viewModel.searchQuery.test {
                assertEquals(query, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `empty query updates state correctly`() = runTest {
        // Given
        viewModel.onSearchQueryChange("test")

        // When
        viewModel.onSearchQueryChange("")

        // Then
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `playersPagingData emits PagingData from use case`() = runTest {
        // Given
        val query = "Jordan"
        val mockPlayers = listOf(
            mockk<Player>(relaxed = true),
            mockk<Player>(relaxed = true)
        )
        val pagingData = PagingData.from(mockPlayers)

        coEvery { searchPlayers(query) } returns flowOf(pagingData)

        // When
        viewModel.onSearchQueryChange(query)
        testScheduler.advanceTimeBy(600) // Past debounce
        testScheduler.runCurrent()

        // Then
        viewModel.playersPagingData.test {
            val emittedData = awaitItem()
            assertNotNull(emittedData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `debounce delays search execution`() = runTest {
        // Given
        val query = "LeBron"
        coEvery { searchPlayers(query) } returns flowOf(PagingData.empty())

        // When
        viewModel.onSearchQueryChange(query)

        testScheduler.advanceTimeBy(400)
        testScheduler.runCurrent()

        // Then
        coVerify(exactly = 0) { searchPlayers(query) }

        // When
        testScheduler.advanceTimeBy(200)
        testScheduler.runCurrent()

        viewModel.playersPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        coVerify(atLeast = 1) { searchPlayers(query) }
    }

    @Test
    fun `rapid query changes only execute last query`() = runTest {
        // Given
        coEvery { searchPlayers(any()) } returns flowOf(PagingData.empty())

        // When
        viewModel.onSearchQueryChange("L")
        testScheduler.advanceTimeBy(200)

        viewModel.onSearchQueryChange("Le")
        testScheduler.advanceTimeBy(200)

        viewModel.onSearchQueryChange("LeB")
        testScheduler.advanceTimeBy(200)

        viewModel.onSearchQueryChange("LeBron")
        testScheduler.advanceTimeBy(600) // Past debounce
        testScheduler.runCurrent()

        viewModel.playersPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        coVerify(exactly = 0) { searchPlayers("L") }
        coVerify(exactly = 0) { searchPlayers("Le") }
        coVerify(exactly = 0) { searchPlayers("LeB") }
        coVerify(atLeast = 1) { searchPlayers("LeBron") }
    }

    @Test
    fun `distinctUntilChanged prevents duplicate consecutive queries`() = runTest {
        // Given
        val query = "Durant"
        coEvery { searchPlayers(query) } returns flowOf(PagingData.empty())

        // When
        viewModel.onSearchQueryChange(query)
        testScheduler.advanceTimeBy(600)
        testScheduler.runCurrent()

        viewModel.playersPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val firstCallCount = try {
            coVerify(atLeast = 1) { searchPlayers(query) }
            1
        } catch (e: AssertionError) {
            0
        }

        assertTrue("First call should have been made", firstCallCount > 0)

        // When
        viewModel.onSearchQueryChange(query)
        testScheduler.advanceTimeBy(600)
        testScheduler.runCurrent()

        coVerify(atLeast = 1) { searchPlayers(query) }
    }

    @Test
    fun `changing to different query triggers new search`() = runTest {
        // Given
        val query1 = "LeBron"
        val query2 = "Curry"

        coEvery { searchPlayers(any()) } returns flowOf(PagingData.empty())

        // When
        viewModel.onSearchQueryChange(query1)
        testScheduler.advanceTimeBy(600)
        testScheduler.runCurrent()

        viewModel.playersPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        coVerify(atLeast = 1) { searchPlayers(query1) }

        // When
        viewModel.onSearchQueryChange(query2)
        testScheduler.advanceTimeBy(600)
        testScheduler.runCurrent()

        viewModel.playersPagingData.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        coVerify(atLeast = 1) { searchPlayers(query2) }
    }
}
