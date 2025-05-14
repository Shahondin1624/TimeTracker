package com.github.shahondin1624.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.shahondin1624.viewmodel.TimeTrackerViewModel

@Composable
@Preview
fun App(vm: TimeTrackerViewModel = TimeTrackerViewModel()) {
    val uiState = vm.uiState.collectAsState()

    MaterialTheme(
        colorScheme = if (uiState.value.isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DarkModeToggle(vm, uiState.value.isDarkMode)
                Stories(vm)
            }
        }
    }
}

@Composable
fun DarkModeToggle(vm: TimeTrackerViewModel, isDarkMode: Boolean) {
    Switch(
        checked = isDarkMode,
        onCheckedChange = { vm.toggleDarkMode() }
    )
}
