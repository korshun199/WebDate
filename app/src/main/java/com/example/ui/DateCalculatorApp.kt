package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AddSubtractScreen
import com.example.ui.screens.DateDifferenceScreen
import com.example.ui.screens.SavedEventsScreen
import com.example.util.DateTimeCalculator
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorApp(
  viewModel: DateCalculatorViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showAboutDialog by remember { mutableStateOf(false) }

  val savedEvents by viewModel.savedEvents.collectAsStateWithLifecycle()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
          ) {
            Text(
              text = "Калькулятор Дат",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Сегодня: ${DateTimeCalculator.formatDate(LocalDate.now())}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showAboutDialog = true },
            modifier = Modifier.testTag("app_info_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "О приложении",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier.testTag("main_bottom_navigation")
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = {
            Icon(Icons.Default.DateRange, contentDescription = "Разница дат")
          },
          label = { Text("Разница дат") },
          modifier = Modifier.testTag("tab_difference")
        )

        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = {
            Icon(Icons.Default.MoreTime, contentDescription = "Сложение")
          },
          label = { Text("+ / – Время") },
          modifier = Modifier.testTag("tab_add_subtract")
        )

        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = {
            if (savedEvents.isNotEmpty()) {
              BadgedBox(
                badge = {
                  Badge { Text("${savedEvents.size}") }
                }
              ) {
                Icon(Icons.Default.AvTimer, contentDescription = "Мои события")
              }
            } else {
              Icon(Icons.Default.AvTimer, contentDescription = "Мои события")
            }
          },
          label = { Text("События") },
          modifier = Modifier.testTag("tab_saved_events")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> DateDifferenceScreen(viewModel = viewModel)
        1 -> AddSubtractScreen(viewModel = viewModel)
        2 -> SavedEventsScreen(viewModel = viewModel)
      }
    }
  }

  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("Калькулятор Дат и Времени") },
      text = {
        androidx.compose.foundation.layout.Column(
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("• Точный подсчет минут, часов, дней и секунд между двумя датами и временем.")
          Text("• Календарный и временной выбор с поддержкой 24-часового формата.")
          Text("• Развернутый расчет: годы, месяцы, дни, недели, рабочие и выходные дни.")
          Text("• Добавление и вычитание произвольного времени из даты.")
          Text("• Сохранение событий и живой обратный отсчет секунд.")
        }
      },
      confirmButton = {
        TextButton(
          onClick = { showAboutDialog = false },
          modifier = Modifier.testTag("close_about_dialog")
        ) {
          Text("Понятно")
        }
      }
    )
  }
}
