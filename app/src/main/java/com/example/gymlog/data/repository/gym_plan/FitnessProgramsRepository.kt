package com.example.gymlog.data.repository.gym_plan

import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.mapper.toDomain
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.repository.FitnessProgramsInterface
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FitnessProgramsRepository @Inject constructor (
    private val gymPlansDao: GymPlansDao,
) : FitnessProgramsInterface {
    //
    override suspend fun getFitnessPrograms(): List<FitnessProgram> {
        return gymPlansDao.getPlanCycles().map { it.toDomain() }.toMutableList()
    }
    //
}