package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.DateCalculatorViewModel
import com.example.ui.components.AppDatePickerDialog
import com.example.ui.components.AppTimePickerDialog
import com.example.ui.components.CalculationResultCard
import com.example.util.DateTimeCalculator
import java.time.LocalDateTime

@Composable
fun DateDifferenceScreen(
  viewModel: DateCalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val startDateTime by viewModel.startDateTime.collectAsStateWithLifecycle()
  val endDateTime by viewModel.endDateTime.collectAsStateWithLifecycle()
  val calculationResult by viewModel.calculationResult.collectAsStateWithLifecycle()

  // Dialog states for Start
  var showStartDatePicker by remember { mutableStateOf(false) }
  var showStartTimePicker by remember { mutableStateOf(false) }

  // Dialog states for End
  var showEndDatePicker by remember { mutableStateOf(false) }
  var showEndTimePicker by remember { mutableStateOf(false) }

  // Save Event Dialog
  var showSaveEventDialog by remember { mutableStateOf(false) }
  var eventTitleInput by remember { mutableStateOf("") }
  var eventCategoryInput by remember { mutableStateOf("Планы") }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header description
    Text(
      text = "Выберите две даты и время из календаря для точного расчета разницы:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Selection Container
    OutlinedCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("date_selection_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.outlinedCardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Start Date & Time Row
        DateTimePickerInputBlock(
          label = "Начальная дата и время",
          dateTime = startDateTime,
          isStart = true,
          onDateClick = { showStartDatePicker = true },
          onTimeClick = { showStartTimePicker = true },
          onNowClick = { viewModel.setStartToNow() },
          dateTestTag = "start_date_picker_trigger",
          timeTestTag = "start_time_picker_trigger",
          nowTestTag = "start_now_button"
        )

        // Swap Button in Center Divider
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
          )

          FilledIconButton(
            onClick = { viewModel.swapStartAndEnd() },
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .testTag("swap_dates_button"),
            colors = IconButtonDefaults.filledIconButtonColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
          ) {
            Icon(
              imageVector = Icons.Default.SwapVert,
              contentDescription = "Поменять даты местами"
            )
          }

          HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
          )
        }

        // End Date & Time Row
        DateTimePickerInputBlock(
          label = "Конечная дата и время",
          dateTime = endDateTime,
          isStart = false,
          onDateClick = { showEndDatePicker = true },
          onTimeClick = { showEndTimePicker = true },
          onNowClick = { viewModel.setEndToNow() },
          dateTestTag = "end_date_picker_trigger",
          timeTestTag = "end_time_picker_trigger",
          nowTestTag = "end_now_button"
        )
      }
    }

    // Quick Presets Scrollable Row
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Быстрый выбор интервала:",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      val chipScrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(chipScrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SuggestionChip(
          onClick = { viewModel.applyPresetPlusDays(1) },
          label = { Text("+1 день") },
          modifier = Modifier.testTag("preset_plus_1_day")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetPlusDays(7) },
          label = { Text("+1 неделя") },
          modifier = Modifier.testTag("preset_plus_7_days")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetPlusDays(30) },
          label = { Text("+30 дней") },
          modifier = Modifier.testTag("preset_plus_30_days")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetPlusMonths(1) },
          label = { Text("+1 месяц") },
          modifier = Modifier.testTag("preset_plus_1_month")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetPlusMonths(6) },
          label = { Text("+6 месяцев") },
          modifier = Modifier.testTag("preset_plus_6_months")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetEndOfMonth() },
          label = { Text("Конец месяца") },
          modifier = Modifier.testTag("preset_end_of_month")
        )
        SuggestionChip(
          onClick = { viewModel.applyPresetNewYear() },
          label = { Text("Новый год") },
          modifier = Modifier.testTag("preset_new_year")
        )
      }
    }

    // Calculation Results Section
    CalculationResultCard(
      result = calculationResult,
      onCopyClicked = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Разница дат", calculationResult.summaryText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Результат скопирован в буфер", Toast.LENGTH_SHORT).show()
      },
      onShareClicked = {
        val sendIntent = Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, calculationResult.summaryText)
          type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться расчетом")
        context.startActivity(shareIntent)
      },
      onSaveClicked = {
        eventTitleInput = "Разница дат: ${DateTimeCalculator.formatDate(startDateTime.toLocalDate())} — ${DateTimeCalculator.formatDate(endDateTime.toLocalDate())}"
        showSaveEventDialog = true
      }
    )

    Spacer(modifier = Modifier.height(24.dp))
  }

  // Start Date Picker Dialog
  if (showStartDatePicker) {
    AppDatePickerDialog(
      initialDate = startDateTime.toLocalDate(),
      onDateSelected = { date -> viewModel.updateStartDate(date) },
      onDismiss = { showStartDatePicker = false }
    )
  }

  // Start Time Picker Dialog
  if (showStartTimePicker) {
    AppTimePickerDialog(
      initialTime = startDateTime.toLocalTime(),
      onTimeSelected = { time -> viewModel.updateStartTime(time) },
      onDismiss = { showStartTimePicker = false }
    )
  }

  // End Date Picker Dialog
  if (showEndDatePicker) {
    AppDatePickerDialog(
      initialDate = endDateTime.toLocalDate(),
      onDateSelected = { date -> viewModel.updateEndDate(date) },
      onDismiss = { showEndDatePicker = false }
    )
  }

  // End Time Picker Dialog
  if (showEndTimePicker) {
    AppTimePickerDialog(
      initialTime = endDateTime.toLocalTime(),
      onTimeSelected = { time -> viewModel.updateEndTime(time) },
      onDismiss = { showEndTimePicker = false }
    )
  }

  // Save Event Dialog
  if (showSaveEventDialog) {
    AlertDialog(
      onDismissRequest = { showSaveEventDialog = false },
      title = { Text("Сохранить событие") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Событие появится во вкладке «Мои события» с обратным отсчетом времени.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          OutlinedTextField(
            value = eventTitleInput,
            onValueChange = { eventTitleInput = it },
            label = { Text("Название события") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_event_title_input")
          )
          OutlinedTextField(
            value = eventCategoryInput,
            onValueChange = { eventCategoryInput = it },
            label = { Text("Категория (например: Отпуск, Работа, Срок)") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_event_category_input")
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.saveEvent(
              title = eventTitleInput,
              targetDateTime = endDateTime,
              startDateTime = startDateTime,
              category = eventCategoryInput
            )
            showSaveEventDialog = false
            Toast.makeText(context, "Событие сохранено!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("confirm_save_event_button")
        ) {
          Text("Сохранить")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showSaveEventDialog = false },
          modifier = Modifier.testTag("cancel_save_event_button")
        ) {
          Text("Отмена")
        }
      }
    )
  }
}

@Composable
fun DateTimePickerInputBlock(
  label: String,
  dateTime: LocalDateTime,
  isStart: Boolean,
  onDateClick: () -> Unit,
  onTimeClick: () -> Unit,
  onNowClick: () -> Unit,
  dateTestTag: String,
  timeTestTag: String,
  nowTestTag: String
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )

      TextButton(
        onClick = onNowClick,
        modifier = Modifier.testTag(nowTestTag)
      ) {
        Icon(
          imageVector = Icons.Default.Today,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Сейчас",
          style = MaterialTheme.typography.labelMedium
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Date Picker Tile
      Card(
        modifier = Modifier
          .weight(1.3f)
          .clickable { onDateClick() }
          .testTag(dateTestTag),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Календарь",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Дата",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
              text = DateTimeCalculator.formatDate(dateTime.toLocalDate()),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Time Picker Tile
      Card(
        modifier = Modifier
          .weight(0.9f)
          .clickable { onTimeClick() }
          .testTag(timeTestTag),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "Время",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Время",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
              text = DateTimeCalculator.formatTime(dateTime.toLocalTime()),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
