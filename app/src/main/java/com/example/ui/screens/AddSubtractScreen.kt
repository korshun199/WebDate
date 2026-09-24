package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AddSubtractOperation
import com.example.ui.DateCalculatorViewModel
import com.example.ui.components.AppDatePickerDialog
import com.example.ui.components.AppTimePickerDialog
import com.example.util.DateTimeCalculator
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AddSubtractScreen(
  viewModel: DateCalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val baseDateTime by viewModel.addSubtractBaseDateTime.collectAsStateWithLifecycle()
  val operation by viewModel.addSubtractOperation.collectAsStateWithLifecycle()
  val inputDays by viewModel.inputDays.collectAsStateWithLifecycle()
  val inputHours by viewModel.inputHours.collectAsStateWithLifecycle()
  val inputMinutes by viewModel.inputMinutes.collectAsStateWithLifecycle()

  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  val targetDateTime = viewModel.calculateTargetDateTime()
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "Прибавление или вычитание дней, часов и минут из исходной даты:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Base Date & Time selector
    OutlinedCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("base_datetime_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.outlinedCardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        DateTimePickerInputBlock(
          label = "Исходная дата и время",
          dateTime = baseDateTime,
          isStart = true,
          onDateClick = { showDatePicker = true },
          onTimeClick = { showTimePicker = true },
          onNowClick = { viewModel.setAddSubtractBaseToNow() },
          dateTestTag = "base_date_picker_trigger",
          timeTestTag = "base_time_picker_trigger",
          nowTestTag = "base_now_button"
        )
      }
    }

    // Operation Selector (Add or Subtract)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FilterChip(
        selected = operation == AddSubtractOperation.ADD,
        onClick = { viewModel.setOperation(AddSubtractOperation.ADD) },
        label = { Text("Прибавить (+)") },
        leadingIcon = {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        modifier = Modifier.weight(1f).testTag("op_add_chip")
      )

      FilterChip(
        selected = operation == AddSubtractOperation.SUBTRACT,
        onClick = { viewModel.setOperation(AddSubtractOperation.SUBTRACT) },
        label = { Text("Вычесть (-)") },
        leadingIcon = {
          Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        modifier = Modifier.weight(1f).testTag("op_subtract_chip")
      )
    }

    // Input Fields: Days, Hours, Minutes
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("duration_inputs_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
      )
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Сколько времени ${if (operation == AddSubtractOperation.ADD) "прибавить" else "вычесть"}:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = inputDays,
            onValueChange = { viewModel.updateInputDays(it) },
            label = { Text("Дни") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("input_days_field")
          )

          OutlinedTextField(
            value = inputHours,
            onValueChange = { viewModel.updateInputHours(it) },
            label = { Text("Часы") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("input_hours_field")
          )

          OutlinedTextField(
            value = inputMinutes,
            onValueChange = { viewModel.updateInputMinutes(it) },
            label = { Text("Минуты") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("input_minutes_field")
          )
        }

        // Quick Day Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SuggestionChip(
            onClick = { viewModel.updateInputDays("1") },
            label = { Text("1 дн.") },
            modifier = Modifier.weight(1f).testTag("quick_1_day")
          )
          SuggestionChip(
            onClick = { viewModel.updateInputDays("7") },
            label = { Text("7 дн.") },
            modifier = Modifier.weight(1f).testTag("quick_7_days")
          )
          SuggestionChip(
            onClick = { viewModel.updateInputDays("30") },
            label = { Text("30 дн.") },
            modifier = Modifier.weight(1f).testTag("quick_30_days")
          )
          SuggestionChip(
            onClick = { viewModel.updateInputDays("90") },
            label = { Text("90 дн.") },
            modifier = Modifier.weight(1f).testTag("quick_90_days")
          )
        }
      }
    }

    // Result Card
    ElevatedCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("add_subtract_result_card"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
      )
    ) {
      Column(
        modifier = Modifier.padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EventAvailable,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Результирующая дата:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val fullFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy г., HH:mm", Locale("ru"))
        val formattedTarget = targetDateTime.format(fullFormatter)

        Text(
          text = formattedTarget.replaceFirstChar { it.uppercase() },
          style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val opWord = if (operation == AddSubtractOperation.ADD) "плюс" else "минус"
        val d = inputDays.toLongOrNull() ?: 0L
        val h = inputHours.toLongOrNull() ?: 0L
        val m = inputMinutes.toLongOrNull() ?: 0L
        val calcSummary = "От: ${DateTimeCalculator.formatDateTime(baseDateTime)}\n" +
            "$opWord ${d} дн. ${h} ч. ${m} мин.\n" +
            "Итог: $formattedTarget"

        Text(
          text = "Исходная: ${DateTimeCalculator.formatDateTime(baseDateTime)} ($opWord ${d}д ${h}ч ${m}м)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilledTonalButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("Расчет даты", calcSummary)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Итоговая дата скопирована", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("copy_target_date_button")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Копировать")
          }

          OutlinedButton(
            onClick = {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, calcSummary)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "Поделиться датой"))
            },
            modifier = Modifier.testTag("share_target_date_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Поделиться")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  if (showDatePicker) {
    AppDatePickerDialog(
      initialDate = baseDateTime.toLocalDate(),
      onDateSelected = { date -> viewModel.setAddSubtractBaseDate(date) },
      onDismiss = { showDatePicker = false }
    )
  }

  if (showTimePicker) {
    AppTimePickerDialog(
      initialTime = baseDateTime.toLocalTime(),
      onTimeSelected = { time -> viewModel.setAddSubtractBaseTime(time) },
      onDismiss = { showTimePicker = false }
    )
  }
}
