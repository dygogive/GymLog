package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plannew.FitnessProgramNew

interface FitnessProgramNewRepositoryInterface {
    suspend fun getAllFitnessProgramsNew(): List<FitnessProgramNew>
}