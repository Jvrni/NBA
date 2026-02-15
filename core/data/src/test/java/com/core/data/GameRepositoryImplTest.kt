package com.core.data

import com.core.data.repository.GameRepositoryImpl
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import com.core.network.dto.TeamDto
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

class GameRepositoryImplTest {

    private lateinit var apiService: NbaApiService
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        dispatcherProvider = TestDispatcherProvider()
        repository = GameRepositoryImpl(apiService, dispatcherProvider)
    }

    @Test
    fun `getTeamGames returns success with game page`() = runTest {
        // Given
        val mockTeam = TeamDto(
            id = 1, abbreviation = "ATL", city = "Atlanta",
            conference = "East", division = "Southeast",
            fullName = "Atlanta Hawks", name = "Hawks"
        )

        val mockGames = listOf(
            GameDto(
                id = 1,
                date = "2024-01-01T00:00:00.000Z",
                homeTeam = mockTeam,
                homeTeamScore = 100,
                visitorTeam = mockTeam.copy(id = 2, fullName = "Boston Celtics"),
                visitorTeamScore = 95,
                season = 2024,
                period = 4,
                status = "Final",
                time = null
            ),
            GameDto(
                id = 2,
                date = "2024-01-05T00:00:00.000Z",
                homeTeam = mockTeam,
                homeTeamScore = 110,
                visitorTeam = mockTeam.copy(id = 3, fullName = "Miami Heat"),
                visitorTeamScore = 105,
                season = 2024,
                period = 4,
                status = "Final",
                time = null
            )
        )

        val mockMeta = MetaDto(
            totalPages = 10,
            currentPage = 1,
            nextPage = 2,
            perPage = 25,
            totalCount = 250
        )

        val mockResponse = GameResponseDto(data = mockGames, meta = mockMeta)

        coEvery { apiService.getGames(1, 1) } returns mockResponse

        // When
        val result = repository.getTeamGames(1, 1)

        // Then
        assertTrue(result is AppResult.Success)
        val gamePage = (result as AppResult.Success).data

        assertEquals(2, gamePage.games.size)
        assertEquals(1, gamePage.currentPage)
        assertEquals(10, gamePage.totalPages)
        assertTrue(gamePage.hasNextPage)

        assertEquals("Atlanta Hawks", gamePage.games[0].homeTeam.fullName)
        assertEquals(100, gamePage.games[0].homeTeamScore)

        coVerify(exactly = 1) { apiService.getGames(1, 1) }
    }

    @Test
    fun `getTeamGames returns error on HttpException`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<Any>(404, "".toResponseBody())
        )
        coEvery { apiService.getGames(1, 1) } throws httpException

        // When
        val result = repository.getTeamGames(1, 1)

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(404, error.code)
    }

    @Test
    fun `getTeamGames returns error on IOException`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { apiService.getGames(1, 1) } throws exception

        // When
        val result = repository.getTeamGames(1, 1)

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertNull(error.code)
        assertTrue(error.message.contains("Network") || error.message.contains("connection"))
    }

    @Test
    fun `getTeamGames with page 2 returns correct page info`() = runTest {
        // Given
        val mockTeam = TeamDto(
            id = 1, abbreviation = "ATL", city = "Atlanta",
            conference = "East", division = "Southeast",
            fullName = "Atlanta Hawks", name = "Hawks"
        )

        val mockGames = listOf(
            GameDto(
                id = 26,
                date = "2024-02-01T00:00:00.000Z",
                homeTeam = mockTeam,
                homeTeamScore = 98,
                visitorTeam = mockTeam.copy(id = 5, fullName = "Chicago Bulls"),
                visitorTeamScore = 92,
                season = 2024,
                period = 4,
                status = "Final",
                time = null
            )
        )

        val mockMeta = MetaDto(
            totalPages = 10,
            currentPage = 2,
            nextPage = 3,
            perPage = 25,
            totalCount = 250
        )

        val mockResponse = GameResponseDto(data = mockGames, meta = mockMeta)

        coEvery { apiService.getGames(1, 2) } returns mockResponse

        // When
        val result = repository.getTeamGames(1, 2)

        // Then
        assertTrue(result is AppResult.Success)
        val gamePage = (result as AppResult.Success).data

        assertEquals(2, gamePage.currentPage)
        assertTrue(gamePage.hasNextPage)

        coVerify(exactly = 1) { apiService.getGames(1, 2) }
    }
}