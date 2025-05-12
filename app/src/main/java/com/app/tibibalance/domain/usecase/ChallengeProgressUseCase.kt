/* domain/usecase/ChallengeProgressUseCase.kt */
package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.ActivityRepository
import com.app.tibibalance.domain.model.Habit
import com.app.tibibalance.domain.model.HabitActivity.Type.COMPLETED
import com.app.tibibalance.domain.model.ChallengeConfig
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map          // ✅
import kotlinx.datetime.*

data class ChallengeProgress(
    val done: Int,
    val target: Int,
    val percent: Float,
    val streak: Int
)

class ChallengeProgressUseCase @Inject constructor(
    private val actRepo: ActivityRepository
) {
    private val zone: TimeZone = TimeZone.currentSystemDefault()

    /** Devuelve un *Flow* reactivo con el progreso del reto para el hábito dado. */
    operator fun invoke(habit: Habit): Flow<ChallengeProgress> =
        actRepo.observe(habit.id).map { acts ->
            /* 1️⃣ número total de eventos COMPLETED */
            val completedActs = acts.filter { it.type == COMPLETED }
            val done = completedActs.size

            /* 2️⃣ cálculo de racha diaria consecutiva */
            val streak = completedActs
                .sortedBy { it.timestamp }
                .fold(0 to LocalDate.fromEpochDays(Int.MIN_VALUE)) { (run, prevDate), act ->
                    val date = act.timestamp.toLocalDateTime(zone).date  // Instant ➜ LocalDate
                    if (date == prevDate.plus(1, DateTimeUnit.DAY)) (run + 1) to date
                    else 1 to date
                }.first

            /* 3️⃣ meta y porcentaje */
            val target = habit.challengeConfig?.targetCount ?: 1
            ChallengeProgress(
                done    = done,
                target  = target,
                percent = done.toFloat() / target,
                streak  = streak
            )
        }
}
