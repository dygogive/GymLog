package com.example.gymlog.data.repository.training_block

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object TrainingBlockRepositoryAdapter {

    @JvmStatic
    fun loadTrainingBlocks(
        gymDayId: Long,
        repository: TrainingBlockRepository,
        callback: TrainingBlocksCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Викликаємо suspend‑метод для отримання даних
                val blocks = repository.getTrainingBlocksByDayId(gymDayId)
                // Повертаємось у головний потік для оновлення UI
                withContext(Dispatchers.Main) {
                    callback.onResult(blocks)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }
}