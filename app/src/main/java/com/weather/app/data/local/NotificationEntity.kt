package com.weather.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted weather tip / notification entry.
 *
 *  - [id] is a deterministic string (e.g. "2026-05-28-SUNNY") so re-generating tips for the same
 *    day with the same type is naturally deduped via [androidx.room.OnConflictStrategy.IGNORE].
 *  - [type] mirrors `domain.model.NotificationType` as a stable string for storage.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val timestampMillis: Long,
    val type: String,
    val message: String,
)
