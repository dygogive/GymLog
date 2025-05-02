package com.example.gymlog.data.repository.training_block_repository

import com.example.gymlog.domain.model.legacy.plan.TrainingBlock

interface TrainingBlocksCallback {
    fun onResult(blocks: List<TrainingBlock>)
    fun onError(exception: Throwable)
}