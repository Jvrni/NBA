package com.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.domain.model.SortType
import com.core.domain.model.Team
import com.core.domain.result.AppResult
import com.core.domain.usecase.GetTeams
import com.core.domain.usecase.SortTeams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTeams: GetTeams,
    private val sortTeams: SortTeams
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _sortType = MutableStateFlow(SortType.NAME)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()
    
    private var allTeams: List<Team> = emptyList()
    
    init {
        loadTeams()
    }
    
    fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            when (val result = getTeams()) {
                is AppResult.Success -> {
                    allTeams = result.data
                    applySorting()
                }
                is AppResult.Error -> {
                    _uiState.value = HomeUiState.Error(result.message)
                }
            }
        }
    }
    
    fun onSortSelected(sortType: SortType) {
        _sortType.value = sortType
        applySorting()
    }
    
    private fun applySorting() {
        val sortedTeams = sortTeams(allTeams, _sortType.value)
        _uiState.value = HomeUiState.Success(sortedTeams)
    }
    
    fun retry() {
        loadTeams()
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val teams: List<Team>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}