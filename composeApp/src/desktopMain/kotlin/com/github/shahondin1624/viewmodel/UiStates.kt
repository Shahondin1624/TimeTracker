package com.github.shahondin1624.viewmodel

import com.github.shahondin1624.Stories

data class TimeTrackerUiState(
    val stories: Stories = Stories(),
    val isDarkMode: Boolean = true
)
