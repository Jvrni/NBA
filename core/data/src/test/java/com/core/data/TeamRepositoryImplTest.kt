package com.core.data

import com.core.data.repository.TeamRepositoryImpl
import com.core.domain.result.AppResult
import com.core.network.api.NbaApiService
import com.core.network.dto.TeamDto
import com.core.network.dto.TeamResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import java.io.IOException
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TeamRepositoryImplTest {

    private lateinit var apiService: NbaApiService
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var repository: TeamRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        dispatcherProvider = TestDispatcherProvider()
        repository = TeamRepositoryImpl(apiService, dispatcherProvider)
    }

    @Test
    fun `getTeams returns success when api call succeeds`() = runTest {
        // Given
        val mockTeams = listOf(
            TeamDto(
                id = 1,
                abbreviation = "ATL",
                city = "Atlanta",
                conference = "East",
                division = "Southeast",
                fullName = "Atlanta Hawks",
                name = "Hawks"
            ),
            TeamDto(
                id = 2,
                abbreviation = "BOS",
                city = "Boston",
                conference = "East",
                division = "Atlantic",
                fullName = "Boston Celtics",
                name = "Celtics"
            )
        )
        val mockResponse = TeamResponseDto(data = mockTeams)

        coEvery { apiService.getTeams() } returns mockResponse

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Success)
        val teams = (result as AppResult.Success).data
        assertEquals(2, teams.size)
        assertEquals("Atlanta Hawks", teams[0].fullName)
        assertEquals("Boston Celtics", teams[1].fullName)

        coVerify(exactly = 1) { apiService.getTeams() }
    }

    @Test
    fun `getTeams returns error with code 404 on HttpException`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<Any>(404, "".toResponseBody())
        )
        coEvery { apiService.getTeams() } throws httpException

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(404, error.code)
        assertTrue(error.message.contains("not found") || error.message.contains("Resource not found"))
        assertNotNull(error.throwable)
    }

    @Test
    fun `getTeams returns error with code 401 on unauthorized`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<Any>(401, "".toResponseBody())
        )
        coEvery { apiService.getTeams() } throws httpException

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(401, error.code)
        assertTrue(error.message.contains("Unauthorized") || error.message.contains("API key"))
    }

    @Test
    fun `getTeams returns error with code 500 on server error`() = runTest {
        // Given
        val httpException = HttpException(
            Response.error<Any>(500, "".toResponseBody())
        )
        coEvery { apiService.getTeams() } throws httpException

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals(500, error.code)
        assertTrue(error.message.contains("Server error") || error.message.contains("try again"))
    }

    @Test
    fun `getTeams returns error on UnknownHostException`() = runTest {
        // Given
        val exception = UnknownHostException("Unable to resolve host")
        coEvery { apiService.getTeams() } throws exception

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertNull(error.code)
        assertTrue(error.message.contains("internet") || error.message.contains("network"))
        assertNotNull(error.throwable)
    }

    @Test
    fun `getTeams returns error on SocketTimeoutException`() = runTest {
        // Given
        val exception = SocketTimeoutException("Timeout")
        coEvery { apiService.getTeams() } throws exception

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertNull(error.code)
        assertTrue(error.message.contains("timeout") || error.message.contains("Timeout"))
    }

    @Test
    fun `getTeams returns error on IOException`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { apiService.getTeams() } throws exception

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertNull(error.code)
        assertTrue(error.message.contains("Network") || error.message.contains("connection"))
    }

    @Test
    fun `getTeams returns error on generic Exception`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery { apiService.getTeams() } throws exception

        // When
        val result = repository.getTeams()

        // Then
        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertEquals("Unexpected error", error.message)
        assertNotNull(error.throwable)
    }
}