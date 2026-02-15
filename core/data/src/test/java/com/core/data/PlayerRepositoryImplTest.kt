package com.core.data

import com.core.data.repository.PlayerRepositoryImpl
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import com.core.network.dto.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PlayerRepositoryImplTest {

    private lateinit var apiService: NbaApiService
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var repository: PlayerRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        dispatcherProvider = TestDispatcherProvider()
        repository = PlayerRepositoryImpl(apiService, dispatcherProvider)
    }

    @Test
    fun `searchPlayers returns success with player page`() = runTest {
        // Given
        val mockTeam = TeamDto(
            id = 14,
            abbreviation = "LAL",
            city = "Los Angeles",
            conference = "West",
            division = "Pacific",
            fullName = "Los Angeles Lakers",
            name = "Lakers"
        )

        val mockPlayers = listOf(
            PlayerDto(
                id = 1,
                firstName = "LeBron",
                lastName = "James",
                position = "F",
                height = 6,
                weight = 250,
                team = mockTeam
            ),
            PlayerDto(
                id = 2,
                firstName = "Anthony",
                lastName = "Davis",
                position = "F-C",
                height = 6,
                weight = 253,
                team = mockTeam
            )
        )

        val mockMeta = MetaDto(
            totalPages = 1,
            currentPage = 1,
            nextPage = null,
            perPage = 25,
            totalCount = 2
        )

        val mockResponse = PlayerResponseDto(data = mockPlayers, meta = mockMeta)

        coEvery { apiService.searchPlayers("james", 1) } returns mockResponse

        // When
        val result = repository.searchPlayers("james", 1)

        // Then
        assertTrue(result is AppResult.Success)
        val playerPage = (result as AppResult.Success).data

        assertEquals(2, playerPage.players.size)
        assertEquals(1, playerPage.currentPage)
        assertEquals(1, playerPage.totalPages)
        assertFalse(playerPage.hasNextPage)

        assertEquals("LeBron", playerPage.players[0].firstName)
        assertEquals("James", playerPage.players[0].lastName)
        assertEquals("Los Angeles Lakers", playerPage.players[0].team.fullName)

        coVerify(exactly = 1) { apiService.searchPlayers("james", 1) }
    }

    @Test
    fun `searchPlayers returns error on HttpException`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<Any>(500, "".toResponseBody())
        )
        coEvery { apiService.searchPlayers("test", 1) } throws httpException

        // When
        val result = repository.searchPlayers("test", 1)

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(500, error.code)
        assertTrue(error.message.contains("Server"))
    }

    @Test
    fun `searchPlayers returns error on IOException`() = runTest {
        // Given
        val exception = IOException("No internet")
        coEvery { apiService.searchPlayers("test", 1) } throws exception

        // When
        val result = repository.searchPlayers("test", 1)

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertNull(error.code)
        assertTrue(error.message.contains("Network") || error.message.contains("connection"))
    }

    @Test
    fun `searchPlayers with empty results returns success with empty list`() = runTest {
        // Given
        val mockMeta = MetaDto(
            totalPages = 0,
            currentPage = 1,
            nextPage = null,
            perPage = 25,
            totalCount = 0
        )

        val mockResponse = PlayerResponseDto(data = emptyList(), meta = mockMeta)

        coEvery { apiService.searchPlayers("xyz123", 1) } returns mockResponse

        // When
        val result = repository.searchPlayers("xyz123", 1)

        // Then
        assertTrue(result is AppResult.Success)
        val playerPage = (result as AppResult.Success).data

        assertTrue(playerPage.players.isEmpty())
        assertEquals(0, playerPage.totalPages)
        assertFalse(playerPage.hasNextPage)
    }
}