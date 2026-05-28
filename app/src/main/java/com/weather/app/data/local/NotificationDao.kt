package com.weather.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    /** Inserts in bulk; existing rows with the same id are silently ignored (daily dedupe). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<NotificationEntity>)

    /** Newest first. Observed reactively so the screen updates as new tips are saved. */
    @Query("SELECT * FROM notifications ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<NotificationEntity>>

    /** Housekeeping — drop anything older than the cutoff. */
    @Query("DELETE FROM notifications WHERE timestampMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
