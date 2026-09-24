package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
  initialDate: LocalDate,
  onDateSelected: (LocalDate) -> Unit,
  onDismiss: () -> Unit
) {
  // Convert local date to UTC epoch millis for Material3 DatePickerState
  val initialEpochMillis = initialDate
    .atStartOfDay(ZoneOffset.UTC)
    .toInstant()
    .toEpochMilli()

  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = initialEpochMillis
  )

  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          datePickerState.selectedDateMillis?.let { millis ->
            val selectedDate = Instant.ofEpochMilli(millis)
              .atZone(ZoneOffset.UTC)
              .toLocalDate()
            onDateSelected(selectedDate)
          }
          onDismiss()
        },
        modifier = Modifier.testTag("date_picker_confirm_button")
      ) {
        Text("Выбрать")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("date_picker_cancel_button")
      ) {
        Text("Отмена")
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
  initialTime: LocalTime,
  onTimeSelected: (LocalTime) -> Unit,
  onDismiss: () -> Unit
) {
  val timePickerState = rememberTimePickerState(
    initialHour = initialTime.hour,
    initialMinute = initialTime.minute,
    is24Hour = true
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Выберите время",
        modifier = Modifier.padding(bottom = 8.dp)
      )
    },
    text = {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        TimePicker(
          state = timePickerState,
          modifier = Modifier.testTag("time_picker_component")
        )
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
          onDismiss()
        },
        modifier = Modifier.testTag("time_picker_confirm_button")
      ) {
        Text("Выбрать")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("time_picker_cancel_button")
      ) {
        Text("Отмена")
      }
    }
  )
}
