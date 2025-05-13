package com.github.shahondin1624.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_LARGE
import com.github.shahondin1624.viewmodel.TimeTrackerViewModel

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleLineActionTextInput(
                        value = newStoryName,
                        onValueChange = { newStoryName = it },
                        onAction = {
                            createStory(
                                vm = vm,
                                newStoryName = newStoryName,
                                resetTextFieldValue = { newStoryName = "" }
                            )
                        },
                        placeholder = { Text("New Story name") },
                        label = { Text("Create a new story") },
                        trailingIcon = {
                            IconButton(onClick = {
                                createStory(
                                    vm = vm,
                                    newStoryName = newStoryName,
                                    resetTextFieldValue = { newStoryName = "" }
                                )
                            }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun createStory(vm: TimeTrackerViewModel, newStoryName: String, resetTextFieldValue: () -> Unit) {
    vm.createStory(newStoryName)
    resetTextFieldValue()
}

@Composable
private fun createHeader() {
    Surface(color = MaterialTheme.colors.background) {
        Column {
            layoutRow(
                column1 = { Text("Title") },
                column2 = { Text("Total") },
                column3 = { Text("Today") },
                column4 = { Text("Elapsed") })
            Spacer(modifier = Modifier.height(SPACER_HEIGHT_LARGE))
        }
    }
}