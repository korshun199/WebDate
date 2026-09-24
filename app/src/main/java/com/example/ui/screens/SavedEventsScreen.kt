package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SavedEventEntity
import com.example.ui.DateCalculatorViewModel
import com.example.ui.components.AppDatePickerDialog
import com.example.ui.components.AppTimePickerDialog
import com.example.util.DateTimeCalculator
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun SavedEventsScreen(
  viewModel: DateCalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val savedEvents by viewModel.savedEvents.collectAsStateWithLifecycle()
  val tickerTime by viewModel.currentTickerTime.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var eventToDelete by remember { mutableStateOf<SavedEventEntity?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        modifier = Modifier.testTag("add_event_fab"),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ) {
        Icon(Icons.Default.Add, contentDescription = "Добавить событие")
      }
    }
  ) { innerPadding ->
    if (savedEvents.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.EventNote,
              contentDescription = null,
              modifier = Modifier.size(36.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "Нет сохраненных событий",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "Сохраняйте важные даты из калькулятора или добавьте отсчет до отпуска, дня рождения или дедлайна!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          TextButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.testTag("empty_state_add_event_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Создать событие")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Сохраненные события и обратный отсчет:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        items(savedEvents, key = { it.id }) { event ->
          EventTickerCard(
            event = event,
            now = tickerTime,
            onDelete = { eventToDelete = event }
          )
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }

  // Delete Confirmation Dialog
  eventToDelete?.let { event ->
    AlertDialog(
      onDismissRequest = { eventToDelete = null },
      title = { Text("Удалить событие?") },
      text = { Text("Вы уверены, что хотите удалить «${event.title}»?") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteEvent(event)
            eventToDelete = null
          },
          modifier = Modifier.testTag("confirm_delete_event_button")
        ) {
          Text("Удалить", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { eventToDelete = null },
          modifier = Modifier.testTag("cancel_delete_event_button")
        ) {
          Text("Отмена")
        }
      }
    )
  }

  // Add Event Dialog
  if (showAddDialog) {
    AddEventDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { title, targetDateTime, category ->
        viewModel.saveEvent(
          title = title,
          targetDateTime = targetDateTime,
          category = category
        )
        showAddDialog = false
      }
    )
  }
}

@Composable
fun EventTickerCard(
  event: SavedEventEntity,
  now: LocalDateTime,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val zone = ZoneId.systemDefault()
  val targetDateTime = Instant.ofEpochMilli(event.targetEpochMillis).atZone(zone).toLocalDateTime()

  val isUpcoming = targetDateTime.isAfter(now)
  val earlier = if (isUpcoming) now else targetDateTime
  val later = if (isUpcoming) targetDateTime else now

  val totalSeconds = ChronoUnit.SECONDS.between(earlier, later)
  val days = totalSeconds / 86400
  val hours = (totalSeconds % 86400) / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("event_card_${event.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (isUpcoming) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = event.category,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isUpcoming) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(32.dp).testTag("delete_event_${event.id}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Удалить событие",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = event.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = DateTimeCalculator.formatDateTime(targetDateTime),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Live Countdown / Count-up Display
      Surface(
        color = if (isUpcoming) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          TickerUnit(value = days.toString(), label = "дней")
          Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
          TickerUnit(value = String.format("%02d", hours), label = "часов")
          Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
          TickerUnit(value = String.format("%02d", minutes), label = "минут")
          Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
          TickerUnit(value = String.format("%02d", seconds), label = "секунд")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (isUpcoming) "⏳ До события осталось" else "✓ Событие уже прошло",
        style = MaterialTheme.typography.labelSmall,
        color = if (isUpcoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun TickerUnit(value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    )
  }
}

@Composable
fun AddEventDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, targetDateTime: LocalDateTime, category: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Планы") }
  var targetDateTime by remember {
    mutableStateOf(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0))
  }

  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  val categories = listOf("Планы", "Отпуск", "День рождения", "Работа", "Праздник")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Новое событие") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Название") },
          singleLine = true,
          placeholder = { Text("Например: Отпуск на море") },
          modifier = Modifier.fillMaxWidth().testTag("add_event_title_input")
        )

        Text(
          text = "Категория:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(3).forEach { cat ->
            SuggestionChip(
              onClick = { category = cat },
              label = { Text(cat) },
              modifier = Modifier.testTag("cat_chip_$cat")
            )
          }
        }

        Text(
          text = "Дата и время события:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable { showDatePicker = true }
              .testTag("add_event_date_trigger"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Дата", style = MaterialTheme.typography.labelSmall)
              Text(
                DateTimeCalculator.formatDate(targetDateTime.toLocalDate()),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Card(
            modifier = Modifier
              .weight(0.7f)
              .clickable { showTimePicker = true }
              .testTag("add_event_time_trigger"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Время", style = MaterialTheme.typography.labelSmall)
              Text(
                DateTimeCalculator.formatTime(targetDateTime.toLocalTime()),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirm(title.ifBlank { "Событие" }, targetDateTime, category)
        },
        modifier = Modifier.testTag("confirm_create_event_button")
      ) {
        Text("Создать")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_create_event_button")
      ) {
        Text("Отмена")
      }
    }
  )

  if (showDatePicker) {
    AppDatePickerDialog(
      initialDate = targetDateTime.toLocalDate(),
      onDateSelected = { date ->
        targetDateTime = LocalDateTime.of(date, targetDateTime.toLocalTime())
      },
      onDismiss = { showDatePicker = false }
    )
  }

  if (showTimePicker) {
    AppTimePickerDialog(
      initialTime = targetDateTime.toLocalTime(),
      onTimeSelected = { time ->
        targetDateTime = LocalDateTime.of(targetDateTime.toLocalDate(), time)
      },
      onDismiss = { showTimePicker = false }
    )
  }
}
