package com.core.data.di

import com.core.data.dispatcher.DefaultDispatcherProvider
import com.core.data.repository.GameRepositoryImpl
import com.core.data.repository.PlayerRepositoryImpl
import com.core.data.repository.TeamRepositoryImpl
import com.core.domain.dispatcher.DispatcherProvider
import com.core.domain.repository.GameRepository
import com.core.domain.repository.PlayerRepository
import com.core.domain.repository.TeamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    
    @Binds
    @Singleton
    abstract fun bindTeamRepository(
        teamRepositoryImpl: TeamRepositoryImpl
    ): TeamRepository
    
    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository
    
    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        defaultDispatcherProvider: DefaultDispatcherProvider
    ): DispatcherProvider
}