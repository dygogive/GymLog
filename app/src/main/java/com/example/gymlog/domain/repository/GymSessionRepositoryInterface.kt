package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.Gym

interface GymSessionRepositoryInterface {
    suspend fun getGymSessionByProgramId(progId: Long): List<Gym>
}
