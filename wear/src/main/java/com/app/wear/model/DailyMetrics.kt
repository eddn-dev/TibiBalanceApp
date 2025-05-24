package com.app.wear.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyMetrics(
  val date: String,
  val steps: Long,
  val activeCalories: Double,
  val exerciseMinutes: Long,
  val avgHeartRate: Double?
)
