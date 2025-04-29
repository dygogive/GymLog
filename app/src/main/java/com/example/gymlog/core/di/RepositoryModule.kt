package com.example.gymlog.core.di

import com.example.gymlog.data.repository.gym_day.GymSessionRepository
import com.example.gymlog.data.repository.gym_plan.FitnessProgramNewRepository
import com.example.gymlog.data.repository.gym_plan.FitnessProgramsRepository
import com.example.gymlog.data.repository.training_block.TrainingBlockRepository
import com.example.gymlog.data.repository.workout.WorkoutRepository
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import com.example.gymlog.domain.repository.FitnessProgramsInterface
import com.example.gymlog.domain.repository.GymSessionRepositoryInterface
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
        impl: TrainingBlockRepository
    ): TrainingBlockRepositoryInterface


    @Binds
    @Singleton
    abstract fun bindFitnessProgramsRepository(
        impl: FitnessProgramsRepository
    ): FitnessProgramsInterface



    @Binds
    @Singleton
    abstract fun bindGymSessionRepository(
        impl: GymSessionRepository
    ): GymSessionRepositoryInterface



    @Binds
    @Singleton
    abstract fun bindFitnessProgramNewRepository(
        impl: FitnessProgramNewRepository
    ): FitnessProgramNewRepositoryInterface





    // якщо згодом з'являться інші domain-інтерфейси, додайте сюди такі ж @Binds методи
}
