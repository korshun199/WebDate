package com.example.model

/**
 * Encapsulates the complete result of calculating the difference between two date-times.
 */
data class DateCalculationResult(
  val totalDays: Long,
  val totalHours: Long,
  val totalMinutes: Long,
  val totalSeconds: Long,
  val totalWeeks: Long,
  val remainingDaysAfterWeeks: Long,
  val workingDays: Long,
  val weekendDays: Long,
  val exactDaysDecimal: Double,
  val years: Int,
  val months: Int,
  val days: Int,
  val hours: Int,
  val minutes: Int,
  val seconds: Int,
  val isNegative: Boolean,
  val humanReadableBreakdown: String,
  val summaryText: String
)
