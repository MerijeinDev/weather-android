package com.weather.app.data.local

import android.content.Context
import androidx.room.Room

/**
 * Manual DI for the Room database. Lazy + application-scoped — there's only ever one instance.
 * Switch to Hilt later if/when the project grows.
 */
object DatabaseModule {

    @Volatile private var INSTANCE: WeatherDatabase? = null

    fun provide(context: Context): WeatherDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather.db",
            )
                .fallbackToDestructiveMigration() // dev convenience; replace with real migrations before release
                .build()
                .also { INSTANCE = it }
        }
    }
}
