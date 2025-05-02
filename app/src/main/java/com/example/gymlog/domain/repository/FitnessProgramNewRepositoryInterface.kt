package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.FitnessProgramNew

interface FitnessProgramNewRepositoryInterface {
    suspend fun getAllFitnessProgramsNew(): List<FitnessProgramNew>
}