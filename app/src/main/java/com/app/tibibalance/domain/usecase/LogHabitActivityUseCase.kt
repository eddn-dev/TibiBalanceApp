package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.ActivityRepository
import com.app.tibibalance.domain.model.HabitActivity
import jakarta.inject.Inject

class LogHabitActivityUseCase @Inject constructor(
    private val repo: ActivityRepository
) {
    suspend operator fun invoke(
        habitId: String,
        type: HabitActivity.Type
    ) = repo.log(HabitActivity(habitId = habitId, type = type))
}

