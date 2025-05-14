package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import com.github.shahondin1624.Stories
import com.github.shahondin1624.Story
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_DEFAULT
import com.github.shahondin1624.UiConstants.Table.ELAPSED_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.RECORD_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.TITLE_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.TOTAL_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.TOTAL_TODAY_COLUMN_WEIGHT
import com.github.shahondin1624.formatWorkTime
import com.github.shahondin1624.viewmodel.TimeTrackerViewModel
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.jetbrains.compose.resources.painterResource
import timetracker.composeapp.generated.resources.Res
import timetracker.composeapp.generated.resources.Stop
import timetracker.composeapp.generated.resources.Timer
import java.time.LocalDateTime
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Story(stories: Stories, vm: TimeTrackerViewModel, index: Int) {
    val story = stories.stories[index]
    logger.debug { "Rendering Story composable for story: ${story.title}, tracking status: ${story.isTracking}, trackedTimes size: ${story.trackedTimes.size}" }

    val totalTimeTracked = story.trackedTimes.filter { it.endTime != null }
        .sumOf { it.endTime!!.toEpochSecond() - it.startTime.toEpochSecond() }
    val timeTrackedToday = story.trackedTimes.filter { it.endTime != null }
        .filter { it.startTime.dayOfYear == LocalDateTime.now().dayOfYear }
        .sumOf { it.endTime!!.toEpochSecond() - it.startTime.toEpochSecond() }

    var showContextMenu by remember { mutableStateOf(false) }
    var showEditTitleDialog by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box {
            layoutRow(
                rowModifier = Modifier.onPointerEvent(PointerEventType.Press) {
                    if (it.buttons.isSecondaryPressed) {
                        showContextMenu = true
                    }
                },
                column1 = {
                    Text(
                        text = story.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = SPACER_HEIGHT_DEFAULT)
                    )
                },
                column2 = {
                    Text(
                        formatWorkTime(totalTimeTracked),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = SPACER_HEIGHT_DEFAULT)
                    )
                },
                column3 = {
                    Text(
                        formatWorkTime(timeTrackedToday),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = SPACER_HEIGHT_DEFAULT)
                    )
                },
                column4 = {
                    ElapsedTimeColumn(story)
                },
                column5 = {
                    val isTracking = story.isTracking
                    val icon = if (isTracking) Res.drawable.Stop else Res.drawable.Timer
                    logger.debug { "Setting icon for story ${story.title}: ${if (isTracking) "Stop" else "Start"}" }
                    IconButton(onClick = { toggleStory(vm, index) }) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = if (isTracking) "Stop" else "Start",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
            ContextMenu(
                showContextMenu = showContextMenu,
                hideContextMenu = { showContextMenu = false },
                showEditTitleDialog = showEditTitleDialog,
                setShowEditTitleDialog = { showEditTitleDialog = it },
                vm = vm,
                index = index,
                story = story
            )
        }
    }
}

@Composable
private fun ContextMenu(
    showContextMenu: Boolean,
    hideContextMenu: () -> Unit,
    showEditTitleDialog: Boolean,
    setShowEditTitleDialog: (Boolean) -> Unit,
    vm: TimeTrackerViewModel,
    index: Int,
    story: Story
) {
    DropdownMenu(
        expanded = showContextMenu,
        onDismissRequest = hideContextMenu
    ) {
        DropdownMenuItem(
            text = { Text("Edit Title", color = MaterialTheme.colorScheme.onSurface) },
            onClick = {
                setShowEditTitleDialog(true)
                hideContextMenu()
            }
        )
        DropdownMenuItem(
            text = { Text("Delete", color = MaterialTheme.colorScheme.onSurface) },
            onClick = {
                vm.deleteStory(index)
                hideContextMenu()
            }
        )
    }

    if (showEditTitleDialog) {
        ShowEditStoryTitleDialog({ setShowEditTitleDialog(false) }, story, vm, index)
    }
}

@Composable
private fun ElapsedTimeColumn(story: Story) {
    if (story.isTracking) {
        var elapsed by remember { mutableStateOf(0L) }

        story.trackedTimes.lastOrNull()?.startTime?.let {
            elapsed = ZonedDateTime.now().toEpochSecond() - it.toEpochSecond()
        }

        LaunchedEffect(true) {
            while (true) {
                delay(1000)
                story.trackedTimes.lastOrNull()?.startTime?.let {
                    elapsed = ZonedDateTime.now().toEpochSecond() - it.toEpochSecond()
                }
            }
        }

        Text(formatWorkTime(elapsed), color = MaterialTheme.colorScheme.onBackground)
    } else {
        logger.debug { "Story ${story.title} not tracking, no elapsed time to display" }
    }
}

@Composable
private fun ShowEditStoryTitleDialog(
    close: () -> Unit,
    story: Story,
    vm: TimeTrackerViewModel,
    index: Int
) {
    var newTitle by remember { mutableStateOf(story.title) }
    AlertDialog(
        onDismissRequest = {
            close()
        },
        title = { Text("Edit Story Title", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            TextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("Title", color = MaterialTheme.colorScheme.onSurface) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newTitle.isNotBlank()) {
                        vm.updateStoryTitle(index, newTitle)
                        close()
                    }
                }
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    close()
                }
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    )
}

private fun toggleStory(vm: TimeTrackerViewModel, index: Int) {
    logger.debug { "toggleStory called with index: $index" }
    vm.toggleTracking(index)
}

@Composable
fun layoutRow(
    column1: (@Composable () -> Unit)? = null,
    column2: (@Composable () -> Unit)? = null,
    column3: (@Composable () -> Unit)? = null,
    column4: (@Composable () -> Unit)? = null,
    column5: (@Composable () -> Unit)? = null,
    rowModifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
    Row(
            modifier = rowModifier.fillMaxWidth().padding(SPACER_HEIGHT_DEFAULT)
        ) {
            Box(modifier = Modifier.weight(TITLE_COLUMN_WEIGHT)) {
                column1?.invoke()
            }
            Box(modifier = Modifier.weight(TOTAL_COLUMN_WEIGHT)) {
                column2?.invoke()
            }
            Box(modifier = Modifier.weight(TOTAL_TODAY_COLUMN_WEIGHT)) {
                column3?.invoke()
            }
            Box(modifier = Modifier.weight(ELAPSED_COLUMN_WEIGHT)) {
                column4?.invoke()
            }
            Box(modifier = Modifier.weight(RECORD_COLUMN_WEIGHT)) {
                column5?.invoke()
            }
        }
    }
}
