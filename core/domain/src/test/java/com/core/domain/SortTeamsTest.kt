package com.core.domain

import com.core.domain.model.SortType
import com.core.domain.model.Team
import com.core.domain.usecase.SortTeams
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class SortTeamsTest {
    
    private lateinit var useCase: SortTeams
    private lateinit var mockTeams: List<Team>
    
    @Before
    fun setup() {
        useCase = SortTeams()
        
        mockTeams = listOf(
            Team(1, "BOS", "Boston", "East", "Atlantic", "Boston Celtics", "Celtics"),
            Team(2, "ATL", "Atlanta", "East", "Southeast", "Atlanta Hawks", "Hawks"),
            Team(3, "LAL", "Los Angeles", "West", "Pacific", "Los Angeles Lakers", "Lakers")
        )
    }
    
    @Test
    fun `sort by name returns alphabetically sorted teams`() {
        // When
        val result = useCase(mockTeams, SortType.NAME)
        
        // Then
        assertEquals("Atlanta Hawks", result[0].fullName)
        assertEquals("Boston Celtics", result[1].fullName)
        assertEquals("Los Angeles Lakers", result[2].fullName)
    }
    
    @Test
    fun `sort by city returns city sorted teams`() {
        // When
        val result = useCase(mockTeams, SortType.CITY)
        
        // Then
        assertEquals("Atlanta", result[0].city)
        assertEquals("Boston", result[1].city)
        assertEquals("Los Angeles", result[2].city)
    }
    
    @Test
    fun `sort by conference returns conference sorted teams`() {
        // When
        val result = useCase(mockTeams, SortType.CONFERENCE)
        
        // Then
        assertEquals("East", result[0].conference)
        assertEquals("East", result[1].conference)
        assertEquals("West", result[2].conference)
    }
}