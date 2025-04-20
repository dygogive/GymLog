package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.GymDay

interface GymSessionRepositoryInterface {
    suspend fun getGymSessionByProgramId(progId: Long): List<GymDay>
}
