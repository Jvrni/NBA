package com.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.domain.model.Game
import com.core.domain.result.AppResult
import com.core.domain.usecase.GetTeamGames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamGamesViewModel @Inject constructor(
    private val useCase: GetTeamGames
) : ViewModel() {
    
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _hasMorePages = MutableStateFlow(false)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()
    
    private var currentTeamId: Int = 0
    private var currentPage: Int = 1
    
    fun loadGames(teamId: Int) {
        currentTeamId = teamId
        currentPage = 1
        _games.value = emptyList()
        
        fetchGames()
    }
    
    fun loadMoreGames() {
        if (_isLoading.value || !_hasMorePages.value) return
        
        currentPage++
        fetchGames()
    }
    
    private fun fetchGames() {
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = useCase(currentTeamId, currentPage)) {
                is AppResult.Success -> {
                    _games.value += result.data.games
                    _hasMorePages.value = result.data.hasNextPage
                }
                is AppResult.Error -> {
                    _hasMorePages.value = false
                }
            }
            
            _isLoading.value = false
        }
    }
}