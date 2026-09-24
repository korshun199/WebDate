package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_events")
data class SavedEventEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val targetEpochMillis: Long,
  val startEpochMillis: Long,
  val category: String = "Событие",
  val notes: String = "",
  val createdAtEpochMillis: Long = System.currentTimeMillis()
)
