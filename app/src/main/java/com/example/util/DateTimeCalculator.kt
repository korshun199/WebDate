package com.example.util

import com.example.model.DateCalculationResult
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

object DateTimeCalculator {

  private val DATE_FORMATTER_SHORT = DateTimeFormatter.ofPattern("d MMM yyyy (EE)", Locale("ru"))
  private val DATE_FORMATTER_LONG = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
  private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale("ru"))
  private val FULL_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("ru"))

  fun formatDate(date: LocalDate): String = date.format(DATE_FORMATTER_SHORT)
  fun formatDateLong(date: LocalDate): String = date.format(DATE_FORMATTER_LONG)
  fun formatTime(time: LocalTime): String = time.format(TIME_FORMATTER)
  fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(FULL_FORMATTER)

  /**
   * Calculates detailed difference between two dates and times.
   */
  fun calculateDifference(
    start: LocalDateTime,
    end: LocalDateTime
  ): DateCalculationResult {
    val isNegative = end.isBefore(start)
    val earlier = if (isNegative) end else start
    val later = if (isNegative) start else end

    val duration = Duration.between(earlier, later)
    val totalSeconds = duration.seconds
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60
    val totalDays = totalHours / 24
    val totalWeeks = totalDays / 7
    val remainingDaysAfterWeeks = totalDays % 7

    val exactDaysDecimal = if (totalSeconds == 0L) 0.0 else (totalSeconds.toDouble() / 86400.0)

    // Working days calculation (Monday - Friday)
    var workingDays = 0L
    var weekendDays = 0L
    var curDate = earlier.toLocalDate()
    val endDateOnly = later.toLocalDate()

    while (curDate.isBefore(endDateOnly)) {
      if (curDate.dayOfWeek == DayOfWeek.SATURDAY || curDate.dayOfWeek == DayOfWeek.SUNDAY) {
        weekendDays++
      } else {
        workingDays++
      }
      curDate = curDate.plusDays(1)
    }

    // Exact calendar breakdown (Years, Months, Days, Hours, Minutes, Seconds)
    var tempTime = earlier
    var years = 0
    while (!tempTime.plusYears(1).isAfter(later)) {
      years++
      tempTime = tempTime.plusYears(1)
    }

    var months = 0
    while (!tempTime.plusMonths(1).isAfter(later)) {
      months++
      tempTime = tempTime.plusMonths(1)
    }

    var days = 0
    while (!tempTime.plusDays(1).isAfter(later)) {
      days++
      tempTime = tempTime.plusDays(1)
    }

    val remDuration = Duration.between(tempTime, later)
    val remSecs = remDuration.seconds
    val hours = (remSecs / 3600).toInt()
    val minutes = ((remSecs % 3600) / 60).toInt()
    val seconds = (remSecs % 60).toInt()

    // Build human readable breakdown string
    val breakdownParts = mutableListOf<String>()
    if (years > 0) breakdownParts.add(pluralize(years.toLong(), "год", "года", "лет"))
    if (months > 0) breakdownParts.add(pluralize(months.toLong(), "месяц", "месяца", "месяцев"))
    if (days > 0) breakdownParts.add(pluralize(days.toLong(), "день", "дня", "дней"))
    if (hours > 0) breakdownParts.add(pluralize(hours.toLong(), "час", "часа", "часов"))
    if (minutes > 0) breakdownParts.add(pluralize(minutes.toLong(), "минута", "минуты", "минут"))
    if (seconds > 0 || breakdownParts.isEmpty()) {
      breakdownParts.add(pluralize(seconds.toLong(), "секунда", "секунды", "секунд"))
    }
    val humanReadableBreakdown = breakdownParts.joinToString(" ")

    // Build summary string for clipboard/sharing
    val summaryBuilder = StringBuilder()
    summaryBuilder.append("Разница во времени:\n")
    summaryBuilder.append("От: ${formatDateTime(start)}\n")
    summaryBuilder.append("До: ${formatDateTime(end)}\n\n")
    if (isNegative) {
      summaryBuilder.append("⚠️ Дата окончания раньше даты начала\n")
    }
    summaryBuilder.append("• Дней: ${formatNumber(totalDays)}\n")
    summaryBuilder.append("• Часов: ${formatNumber(totalHours)}\n")
    summaryBuilder.append("• Минут: ${formatNumber(totalMinutes)}\n")
    summaryBuilder.append("• Секунд: ${formatNumber(totalSeconds)}\n")
    summaryBuilder.append("• В неделях: ${totalWeeks} нед. ${remainingDaysAfterWeeks} дн.\n")
    summaryBuilder.append("• Рабочих дней: $workingDays (выходных: $weekendDays)\n")
    summaryBuilder.append("• Точный период: $humanReadableBreakdown\n")

    return DateCalculationResult(
      totalDays = totalDays,
      totalHours = totalHours,
      totalMinutes = totalMinutes,
      totalSeconds = totalSeconds,
      totalWeeks = totalWeeks,
      remainingDaysAfterWeeks = remainingDaysAfterWeeks,
      workingDays = workingDays,
      weekendDays = weekendDays,
      exactDaysDecimal = exactDaysDecimal,
      years = years,
      months = months,
      days = days,
      hours = hours,
      minutes = minutes,
      seconds = seconds,
      isNegative = isNegative,
      humanReadableBreakdown = humanReadableBreakdown,
      summaryText = summaryBuilder.toString()
    )
  }

  fun pluralize(count: Long, one: String, twoFour: String, many: String): String {
    val absVal = abs(count)
    val rem100 = absVal % 100
    val rem10 = absVal % 10

    val form = when {
      rem100 in 11..19 -> many
      rem10 == 1L -> one
      rem10 in 2..4 -> twoFour
      else -> many
    }
    return "${formatNumber(count)} $form"
  }

  fun formatNumber(number: Long): String {
    return String.format(Locale("ru"), "%,d", number).replace(',', ' ')
  }

  fun formatDecimal(value: Double): String {
    return String.format(Locale("ru"), "%.2f", value)
  }
}
