package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SavedEventEntity
import com.example.model.DateCalculationResult
import com.example.util.DateTimeCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

enum class AddSubtractOperation {
  ADD, SUBTRACT
}

class DateCalculatorViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val eventDao = database.savedEventDao()

  // Primary Start Date & Time
  private val _startDateTime = MutableStateFlow(
    LocalDateTime.now().withSecond(0).withNano(0)
  )
  val startDateTime: StateFlow<LocalDateTime> = _startDateTime.asStateFlow()

  // Primary End Date & Time (Defaults to 7 days from now at the same time)
  private val _endDateTime = MutableStateFlow(
    LocalDateTime.now().plusDays(7).withSecond(0).withNano(0)
  )
  val endDateTime: StateFlow<LocalDateTime> = _endDateTime.asStateFlow()

  // Calculation Result derived from start and end
  val calculationResult: StateFlow<DateCalculationResult> = combine(
    _startDateTime,
    _endDateTime
  ) { start, end ->
    DateTimeCalculator.calculateDifference(start, end)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DateTimeCalculator.calculateDifference(
      _startDateTime.value,
      _endDateTime.value
    )
  )

  // Add / Subtract Calculator Mode State
  private val _addSubtractBaseDateTime = MutableStateFlow(
    LocalDateTime.now().withSecond(0).withNano(0)
  )
  val addSubtractBaseDateTime: StateFlow<LocalDateTime> = _addSubtractBaseDateTime.asStateFlow()

  private val _addSubtractOperation = MutableStateFlow(AddSubtractOperation.ADD)
  val addSubtractOperation: StateFlow<AddSubtractOperation> = _addSubtractOperation.asStateFlow()

  private val _inputDays = MutableStateFlow("30")
  val inputDays: StateFlow<String> = _inputDays.asStateFlow()

  private val _inputHours = MutableStateFlow("0")
  val inputHours: StateFlow<String> = _inputHours.asStateFlow()

  private val _inputMinutes = MutableStateFlow("0")
  val inputMinutes: StateFlow<String> = _inputMinutes.asStateFlow()

  // Saved Events from Database
  val savedEvents: StateFlow<List<SavedEventEntity>> = eventDao.getAllEvents()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Current time ticker for live countdowns (updates every second)
  private val _currentTickerTime = MutableStateFlow(LocalDateTime.now())
  val currentTickerTime: StateFlow<LocalDateTime> = _currentTickerTime.asStateFlow()

  init {
    viewModelScope.launch {
      while (true) {
        delay(1000)
        _currentTickerTime.value = LocalDateTime.now()
      }
    }
  }

  // Setters for Start Date & Time
  fun updateStartDate(date: LocalDate) {
    val current = _startDateTime.value
    _startDateTime.value = LocalDateTime.of(date, current.toLocalTime())
  }

  fun updateStartTime(time: LocalTime) {
    val current = _startDateTime.value
    _startDateTime.value = LocalDateTime.of(current.toLocalDate(), time)
  }

  fun setStartToNow() {
    _startDateTime.value = LocalDateTime.now().withSecond(0).withNano(0)
  }

  // Setters for End Date & Time
  fun updateEndDate(date: LocalDate) {
    val current = _endDateTime.value
    _endDateTime.value = LocalDateTime.of(date, current.toLocalTime())
  }

  fun updateEndTime(time: LocalTime) {
    val current = _endDateTime.value
    _endDateTime.value = LocalDateTime.of(current.toLocalDate(), time)
  }

  fun setEndToNow() {
    _endDateTime.value = LocalDateTime.now().withSecond(0).withNano(0)
  }

  // Swap Start and End dates
  fun swapStartAndEnd() {
    val temp = _startDateTime.value
    _startDateTime.value = _endDateTime.value
    _endDateTime.value = temp
  }

  // Presets
  fun applyPresetPlusDays(days: Long) {
    _endDateTime.value = _startDateTime.value.plusDays(days)
  }

  fun applyPresetPlusHours(hours: Long) {
    _endDateTime.value = _startDateTime.value.plusHours(hours)
  }

  fun applyPresetPlusMonths(months: Long) {
    _endDateTime.value = _startDateTime.value.plusMonths(months)
  }

  fun applyPresetEndOfMonth() {
    val startDate = _startDateTime.value.toLocalDate()
    val endOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth())
    _endDateTime.value = LocalDateTime.of(endOfMonth, LocalTime.of(23, 59))
  }

  fun applyPresetNewYear() {
    val current = _startDateTime.value
    val nextNewYear = LocalDate.of(current.year + 1, 1, 1)
    _endDateTime.value = LocalDateTime.of(nextNewYear, LocalTime.MIDNIGHT)
  }

  // Add / Subtract operations
  fun setAddSubtractBaseDate(date: LocalDate) {
    val current = _addSubtractBaseDateTime.value
    _addSubtractBaseDateTime.value = LocalDateTime.of(date, current.toLocalTime())
  }

  fun setAddSubtractBaseTime(time: LocalTime) {
    val current = _addSubtractBaseDateTime.value
    _addSubtractBaseDateTime.value = LocalDateTime.of(current.toLocalDate(), time)
  }

  fun setAddSubtractBaseToNow() {
    _addSubtractBaseDateTime.value = LocalDateTime.now().withSecond(0).withNano(0)
  }

  fun setOperation(op: AddSubtractOperation) {
    _addSubtractOperation.value = op
  }

  fun updateInputDays(value: String) {
    _inputDays.value = value.filter { it.isDigit() }
  }

  fun updateInputHours(value: String) {
    _inputHours.value = value.filter { it.isDigit() }
  }

  fun updateInputMinutes(value: String) {
    _inputMinutes.value = value.filter { it.isDigit() }
  }

  fun calculateTargetDateTime(): LocalDateTime {
    val days = _inputDays.value.toLongOrNull() ?: 0L
    val hours = _inputHours.value.toLongOrNull() ?: 0L
    val minutes = _inputMinutes.value.toLongOrNull() ?: 0L
    val base = _addSubtractBaseDateTime.value

    return when (_addSubtractOperation.value) {
      AddSubtractOperation.ADD -> base.plusDays(days).plusHours(hours).plusMinutes(minutes)
      AddSubtractOperation.SUBTRACT -> base.minusDays(days).minusHours(hours).minusMinutes(minutes)
    }
  }

  // Saved Events Database Operations
  fun saveEvent(
    title: String,
    targetDateTime: LocalDateTime,
    startDateTime: LocalDateTime = LocalDateTime.now(),
    category: String = "Событие",
    notes: String = ""
  ) {
    viewModelScope.launch {
      val zone = java.time.ZoneId.systemDefault()
      val targetEpoch = targetDateTime.atZone(zone).toInstant().toEpochMilli()
      val startEpoch = startDateTime.atZone(zone).toInstant().toEpochMilli()

      eventDao.insertEvent(
        SavedEventEntity(
          title = title.ifBlank { "Моё событие" },
          targetEpochMillis = targetEpoch,
          startEpochMillis = startEpoch,
          category = category,
          notes = notes
        )
      )
    }
  }

  fun deleteEvent(event: SavedEventEntity) {
    viewModelScope.launch {
      eventDao.deleteEvent(event)
    }
  }
}
