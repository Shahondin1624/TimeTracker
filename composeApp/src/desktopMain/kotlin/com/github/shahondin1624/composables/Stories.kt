package com.github.shahondin1624.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_DEFAULT
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_LARGE
import com.github.shahondin1624.formatWorkTime
import com.github.shahondin1624.viewmodel.TimeTrackerViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Stories(vm: TimeTrackerViewModel) {
    val uiState = vm.uiState.collectAsState()
    var newStoryName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                createHeader()
            }
            items(uiState.value.stories.stories.size) { index ->
                Story(uiState.value.stories, vm, index)
            }
            item {
                CreateAddNewItemRow(vm, newStoryName) {
                    newStoryName = it
                }
            }
        }
    }
}

@Composable
private fun CreateAddNewItemRow(vm: TimeTrackerViewModel, newStoryName: String, setNewStoryName: (String) -> Unit) {
    Spacer(modifier = Modifier.height(SPACER_HEIGHT_LARGE))
    val uiState = vm.uiState.collectAsState()
    layoutRow(
        column1 = {
            SingleLineActionTextInput(
                value = newStoryName,
                onValueChange = { setNewStoryName(it) },
                onAction = {
                    if (newStoryName.isNotBlank()) {
                        createStory(
                            vm = vm,
                            newStoryName = newStoryName,
                            resetTextFieldValue = { setNewStoryName("") }
                        )
                    }
                },
                placeholder = { Text("New Story name") },
                label = { Text("Create a new story") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (newStoryName.isNotBlank()) {
                            createStory(
                                vm = vm,
                                newStoryName = newStoryName,
                                resetTextFieldValue = { setNewStoryName("") }
                            )
                        }
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                }
            )
        },
        column2 = {
            val totalTimeTracked =
                uiState.value.stories.stories.sumOf { story ->
                    story.trackedTimes
                        .filter { it.endTime != null }
                        .sumOf {
                        it.endTime!!.toEpochSecond() - it.startTime.toEpochSecond()
                    }
                }
            Text(
                text = formatWorkTime(totalTimeTracked),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = SPACER_HEIGHT_DEFAULT)
            )
        },
        column3 = {
            val totalTimeTrackedToday =
                uiState.value.stories.stories.sumOf { story ->
                    story.trackedTimes
                        .filter { it.startTime.dayOfYear == LocalDateTime.now().dayOfYear
                                && it.endTime != null }
                        .sumOf {
                            it.endTime!!.toEpochSecond() - it.startTime.toEpochSecond()
                        }
                }
            Text(
                text = formatWorkTime(totalTimeTrackedToday),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = SPACER_HEIGHT_DEFAULT)
            )
        })
}

private fun createStory(vm: TimeTrackerViewModel, newStoryName: String, resetTextFieldValue: () -> Unit) {
    vm.createStory(newStoryName)
    resetTextFieldValue()
}

@Composable
private fun createHeader() {
    Column {
        layoutRow(
            column1 = { Text("Title") },
            column2 = { Text("Total") },
            column3 = { Text("Today") },
            column4 = { Text("Elapsed") })
        Spacer(modifier = Modifier.height(SPACER_HEIGHT_LARGE))
    }
}
