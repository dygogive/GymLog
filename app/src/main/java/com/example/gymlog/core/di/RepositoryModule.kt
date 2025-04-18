package com.example.gymlog.core.di

import com.example.gymlog.data.repository.workout.WorkoutRepository
import com.example.gymlog.domain.repository.TrainingBlockRepositoryInterface
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
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
    abstract fun bindWorkoutRepository(
        impl: WorkoutRepository
    ): WorkoutRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindTrainingBlockRepository(
        impl: com.example.gymlog.data.repository.plan.TrainingBlockRepository
    ): TrainingBlockRepositoryInterface

    // якщо згодом з'являться інші domain-інтерфейси, додайте сюди такі ж @Binds методи
}
