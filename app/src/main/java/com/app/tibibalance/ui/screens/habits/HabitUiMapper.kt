/* ui/screens/habits/HabitUiMapper.kt */
package com.app.tibibalance.ui.screens.habits

import com.app.tibibalance.domain.model.Habit

fun Habit.toUi() = HabitUi(
    id      = id,
    name    = name,
    icon    = icon,
    category= category.name
)
