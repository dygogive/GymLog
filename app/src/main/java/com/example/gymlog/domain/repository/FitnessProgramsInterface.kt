package com.example.gymlog.domain.repository

import com.example.gymlog.domain.model.plan.FitnessProgram

interface FitnessProgramsInterface {
    suspend fun getFitnessPrograms(): List<FitnessProgram>
}