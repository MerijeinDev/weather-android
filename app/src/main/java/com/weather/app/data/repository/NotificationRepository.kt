package com.weather.app.data.repository

import com.weather.app.data.local.NotificationDao
import com.weather.app.data.local.NotificationEntity
import com.weather.app.domain.model.NotificationType
import com.weather.app.domain.model.WeatherNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class NotificationRepository(
    private val dao: NotificationDao,
) {
    /** Observe all notifications, newest first. */
    fun observeAll(): Flow<List<WeatherNotification>> =
        dao.observeAll().map { rows -> rows.mapNotNull { it.toDomain() } }

    /** Bulk save; existing (id) rows are ignored. */
    suspend fun saveAll(items: List<WeatherNotification>) {
        if (items.isEmpty()) return
        dao.insertAll(items.map { it.toEntity() })
    }

    /** Optional housekeeping — call e.g. once a week to drop tips older than [cutoff]. */
    suspend fun purgeOlderThan(cutoff: Instant) {
        dao.deleteOlderThan(cutoff.toEpochMilli())
    }

    private fun NotificationEntity.toDomain(): WeatherNotification? {
        val type = NotificationType.fromName(type) ?: return null
        return WeatherNotification(
            id = id,
            createdAt = Instant.ofEpochMilli(timestampMillis),
            type = type,
            message = message,
        )
    }

    private fun WeatherNotification.toEntity() = NotificationEntity(
        id = id,
        timestampMillis = createdAt.toEpochMilli(),
        type = type.name,
        message = message,
    )
}
