package com.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.core.domain.model.Game
import com.core.domain.usecase.GetTeamGames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class TeamGamesViewModel @Inject constructor(
    private val useCase: GetTeamGames
) : ViewModel() {

    private val _teamId = MutableStateFlow<Int?>(null)

    val gamesPagingData: Flow<PagingData<Game>> = _teamId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(PagingData.empty())
            } else {
                useCase(id)
            }
        }
        .cachedIn(viewModelScope)

    fun loadGames(teamId: Int) {
        _teamId.value = teamId
    }
}