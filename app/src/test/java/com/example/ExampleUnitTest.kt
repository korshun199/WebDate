package com.example

import com.example.util.DateTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ExampleUnitTest {

  @Test
  fun testCalculateDifference_sameDayHoursMinutes() {
    val start = LocalDateTime.of(2026, 9, 24, 10, 0)
    val end = LocalDateTime.of(2026, 9, 24, 15, 30)

    val result = DateTimeCalculator.calculateDifference(start, end)

    assertEquals(0L, result.totalDays)
    assertEquals(5L, result.totalHours)
    assertEquals(330L, result.totalMinutes)
    assertEquals(19800L, result.totalSeconds)
    assertFalse(result.isNegative)
  }

  @Test
  fun testCalculateDifference_multipleDays() {
    val start = LocalDateTime.of(2026, 9, 20, 12, 0)
    val end = LocalDateTime.of(2026, 9, 27, 12, 0)

    val result = DateTimeCalculator.calculateDifference(start, end)

    assertEquals(7L, result.totalDays)
    assertEquals(168L, result.totalHours)
    assertEquals(10080L, result.totalMinutes)
    assertEquals(1L, result.totalWeeks)
    assertEquals(0L, result.remainingDaysAfterWeeks)
  }

  @Test
  fun testCalculateDifference_negativeOrder() {
    val start = LocalDateTime.of(2026, 9, 25, 10, 0)
    val end = LocalDateTime.of(2026, 9, 20, 10, 0)

    val result = DateTimeCalculator.calculateDifference(start, end)

    assertTrue(result.isNegative)
    assertEquals(5L, result.totalDays)
    assertEquals(120L, result.totalHours)
  }

  @Test
  fun testPluralizeRussian() {
    assertEquals("1 день", DateTimeCalculator.pluralize(1, "день", "дня", "дней"))
    assertEquals("2 дня", DateTimeCalculator.pluralize(2, "день", "дня", "дней"))
    assertEquals("4 дня", DateTimeCalculator.pluralize(4, "день", "дня", "дней"))
    assertEquals("5 дней", DateTimeCalculator.pluralize(5, "день", "дня", "дней"))
    assertEquals("11 дней", DateTimeCalculator.pluralize(11, "день", "дня", "дней"))
    assertEquals("21 день", DateTimeCalculator.pluralize(21, "день", "дня", "дней"))
    assertEquals("24 дня", DateTimeCalculator.pluralize(24, "день", "дня", "дней"))

    assertEquals("1 час", DateTimeCalculator.pluralize(1, "час", "часа", "часов"))
    assertEquals("3 часа", DateTimeCalculator.pluralize(3, "час", "часа", "часов"))
    assertEquals("10 часов", DateTimeCalculator.pluralize(10, "час", "часа", "часов"))

    assertEquals("1 минута", DateTimeCalculator.pluralize(1, "минута", "минуты", "минут"))
    assertEquals("2 минуты", DateTimeCalculator.pluralize(2, "минута", "минуты", "минут"))
    assertEquals("15 минут", DateTimeCalculator.pluralize(15, "минута", "минуты", "минут"))
  }
}
