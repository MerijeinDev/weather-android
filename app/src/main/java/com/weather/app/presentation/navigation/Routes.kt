package com.weather.app.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe Compose Navigation routes for the weather feature.
 * Add new screens here as data objects (or data classes with args).
 */
@Serializable
data object HomeRoute

@Serializable
data object DetailRoute

@Serializable
data object NotificationsRoute
