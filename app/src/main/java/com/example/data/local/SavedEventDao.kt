package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedEventDao {
  @Query("SELECT * FROM saved_events ORDER BY targetEpochMillis ASC")
  fun getAllEvents(): Flow<List<SavedEventEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: SavedEventEntity): Long

  @Delete
  suspend fun deleteEvent(event: SavedEventEntity)

  @Query("DELETE FROM saved_events WHERE id = :id")
  suspend fun deleteById(id: Long)
}
