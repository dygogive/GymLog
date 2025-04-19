package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.GymSession

interface GymSessionRepositoryInterface {
    suspend fun getGymSessionByProgramId(progId: Long): List<GymSession>
}
