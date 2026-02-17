package com.core.network.api

import com.core.network.dto.GameResponseDto
import com.core.network.dto.PlayerResponseDto
import com.core.network.dto.TeamResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NbaApiService {

    @GET("v1/teams")
    suspend fun getTeams(): TeamResponseDto

    @GET("v1/games")
    suspend fun getGames(
        @Query("team_ids[]") teamId: Int,
        @Query("cursor") cursor: Int = 0,
        @Query("per_page") perPage: Int = 25
    ): GameResponseDto

    @GET("v1/players")
    suspend fun searchPlayers(
        @Query("search") playerName: String,
        @Query("cursor") cursor: Int = 0,
        @Query("per_page") perPage: Int = 25
    ): PlayerResponseDto
}