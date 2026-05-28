package com.weather.app.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class NotificationsViewModel(
    private val repository: NotificationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { items ->
                val now = Instant.now()
                val (newItems, earlier) = items.partition {
                    Duration.between(it.createdAt, now) <= NEW_WINDOW
                }
                _state.value = NotificationsUiState(
                    isLoading = false,
                    newItems = newItems,
                    earlierItems = earlier,
                )
            }
        }
    }

    private companion object {
        /** Anything created within the last hour shows up under "New". */
        val NEW_WINDOW: Duration = Duration.ofHours(1)
    }
}
