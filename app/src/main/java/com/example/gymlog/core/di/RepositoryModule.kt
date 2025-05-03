package com.example.gymlog.core.di

import com.example.gymlog.data.repository.FitnessProgramNewRepository
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для зв'язування інтерфейсів репозиторіїв із їх реалізаціями.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {




    @Binds
    @Singleton
    abstract fun bindFitnessProgramNewRepository(
        impl: FitnessProgramNewRepository
    ): FitnessProgramNewRepositoryInterface


}
