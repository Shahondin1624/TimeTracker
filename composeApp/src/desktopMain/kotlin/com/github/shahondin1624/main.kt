package com.github.shahondin1624

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.shahondin1624.composables.App
import com.github.shahondin1624.viewmodel.TimeTrackerUiState
import com.github.shahondin1624.viewmodel.TimeTrackerViewModel
import mu.KotlinLogging
import org.jetbrains.compose.resources.painterResource
import timetracker.composeapp.generated.resources.Hourglass_Win
import timetracker.composeapp.generated.resources.Res
import java.io.File
import java.io.IOException
import java.nio.file.Files

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = application {
    val debugMode = args.contains("debug")
    val viewModel = createViewModel(debugMode)

    Window(
        onCloseRequest = {
            val fileToStoreIn = if (debugMode) {
                getDebuggingFile()
            } else {
                getTrackedTimesFile()
            }
            saveAllTracking(viewModel)
            saveViewModelToFile(viewModel, fileToStoreIn)
            exitApplication()
        },
        title = "TimeTracker",
        icon = painterResource(Res.drawable.Hourglass_Win),
    ) {
        App(vm = viewModel)
    }
}

private fun createViewModel(debugMode: Boolean): TimeTrackerViewModel {
    return loadViewModelFromFile(debugMode)
}

private fun getTrackedTimesFile(): File {
    val homeDir = System.getProperty("user.home")
    val timeTrackerDir = File(homeDir, ".TimeTracker")

    if (!timeTrackerDir.exists()) {
        val dirCreated = timeTrackerDir.mkdir()
        if (!dirCreated) {
            logger.error { "Failed to create directory: ${timeTrackerDir.absolutePath}" }
        } else {
            when(getOS()) {
                OS.WINDOWS -> Files.setAttribute(timeTrackerDir.toPath(), "dos:hidden", true)
                OS.LINUX -> {} // In Linux, directories starting with a dot are already hidden
            }
        }
    }

    val trackedTimesFile = File(timeTrackerDir, "TrackedTimes.json")
    if (!trackedTimesFile.exists()) {
        try {
            trackedTimesFile.createNewFile()
        } catch (e: IOException) {
            logger.error(e) { "Failed to create file: ${trackedTimesFile.absolutePath}" }
        }
    }

    return trackedTimesFile
}

private fun loadViewModelFromFile(isInDebugMode: Boolean = false): TimeTrackerViewModel {
    val vm = TimeTrackerViewModel()

    val trackedTimesFile = if (isInDebugMode) {
        getDebuggingFile()
    } else {
        getTrackedTimesFile()
    }

    if (trackedTimesFile.exists()) {
        try {
            val jsonContent = trackedTimesFile.readText()
            if (jsonContent.isNotEmpty()) {
                try {
                    val stories = Stories.deserialize(jsonContent)
                    vm.updateState(TimeTrackerUiState(stories))
                    logger.info { "Successfully loaded tracked times from file: ${trackedTimesFile.absolutePath}" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to deserialize tracked times from file: ${trackedTimesFile.absolutePath}" }
                    // Return empty viewmodel if deserialization fails
                }
            } else {
                logger.info { "TrackedTimes.json file is empty, using empty viewmodel" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to read tracked times from file: ${trackedTimesFile.absolutePath}" }
        }
    } else {
        logger.info { "TrackedTimes.json file does not exist, using empty viewmodel" }
    }

    return vm
}

private fun getDebuggingFile(): File =
    File(System.getProperty("user.dir"), "src/commonMain/composeResources/files/debug-data")

private fun saveViewModelToFile(viewModel: TimeTrackerViewModel, trackedTimesFile: File = getTrackedTimesFile()) {
    try {
        val stories = viewModel.uiState.value.stories
        val jsonContent = stories.serialize()
        trackedTimesFile.writeText(jsonContent)
        logger.info { "Saved tracked times to file: ${trackedTimesFile.absolutePath}" }
    } catch (e: IOException) {
        logger.error(e) { "Failed to save tracked times to file: ${trackedTimesFile.absolutePath}" }
    }
}

private fun saveAllTracking(viewModel: TimeTrackerViewModel) {
    viewModel.indexOfStillTrackingStory().let { index ->
        if (index != -1) {
            viewModel.toggleTracking(index)
        }
    }
}
