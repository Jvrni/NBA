package com.core.domain

import com.core.domain.repository.PlayerRepository
import com.core.domain.usecase.SearchPlayers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
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
    fun `invoke with blank query returns failure`() = runTest {
        // When
        val result = useCase("   ")
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        
        coVerify(exactly = 0) { playerRepository.searchPlayers(any(), any()) }
    }
    
    @Test
    fun `invoke trims query before calling repository`() = runTest {
        // Given
        coEvery { 
            playerRepository.searchPlayers("james", 1) 
        } returns Result.success(mockk())
        
        // When
        useCase("  james  ")
        
        // Then
        coVerify { playerRepository.searchPlayers("james", 1) }
    }
}