package com.core.domain

import androidx.paging.PagingData
import app.cash.turbine.test
import com.core.domain.model.Player
import com.core.domain.repository.PlayerRepository
import com.core.domain.usecase.SearchPlayers
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SearchPlayersTest {

    private lateinit var playerRepository: PlayerRepository
    private lateinit var useCase: SearchPlayers

    @Before
    fun setup() {
        playerRepository = mockk()
        useCase = SearchPlayers(playerRepository)
    }

    @Test
    fun `invoke delegates to repository`() = runTest {
        // Given
        val query = "LeBron"
        val mockPlayers = listOf(mockk<Player>(relaxed = true))
        val pagingData = PagingData.from(mockPlayers)

        every { playerRepository.searchPlayers(query) } returns flowOf(pagingData)

        // When
        val result = useCase(query)

        // Then
        assertNotNull(result)
        result.test {
            val emittedData = awaitItem()
            assertNotNull(emittedData)
            cancelAndIgnoreRemainingEvents()
        }

        verify { playerRepository.searchPlayers(query) }
    }

    @Test
    fun `invoke with empty query returns flow`() = runTest {
        // Given
        val query = ""
        val pagingData = PagingData.empty<Player>()

        every { playerRepository.searchPlayers(query) } returns flowOf(pagingData)

        // When
        val result = useCase(query)

        // Then
        assertNotNull(result)
        verify { playerRepository.searchPlayers(query) }
    }
}
