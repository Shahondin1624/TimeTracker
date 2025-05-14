package com.github.shahondin1624.viewmodel

import androidx.lifecycle.ViewModel
import com.github.shahondin1624.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimeTrackerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TimeTrackerUiState())
    val uiState: StateFlow<TimeTrackerUiState> = _uiState.asStateFlow()

    fun createStory(title: String) {
        val lastId = _uiState.value.stories.stories.maxOfOrNull { it.id } ?: 0
        _uiState.update {
            it.copy(stories = it.stories.addStory(
                Story(
                    id = lastId + 1,
                    title = title
                )
            ))
        }
    }

    fun toggleTracking(index: Int) {
        _uiState.update {
            it.copy(stories = it.stories.toggleTracking(index))
        }
    }

    fun updateState(newState: TimeTrackerUiState) {
        _uiState.value = newState
    }

    fun deleteStory(index: Int) {
        _uiState.update {
            val storyToRemove = it.stories.stories.getOrNull(index) ?: return@update it
            it.copy(stories = it.stories.removeStory(storyToRemove))
        }
    }

    fun updateStoryTitle(index: Int, newTitle: String) {
        _uiState.update {
            it.copy(stories = it.stories.updateStoryTitle(index, newTitle))
        }
    }

    fun indexOfStillTrackingStory(): Int {
        return uiState.value.stories.stories.indexOfFirst { it.isTracking }
    }
}
