package com.example.gymlog.data.repository.training_block

import com.example.gymlog.domain.model.plan.TrainingBlock

interface TrainingBlocksCallback {
    fun onResult(blocks: List<TrainingBlock>)
    fun onError(exception: Throwable)
}