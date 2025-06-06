package com.github.shahondin1624

fun formatWorkTime(seconds: Long): String {
    val secondsInWorkDay = 8 * 3600L
    val days = seconds / secondsInWorkDay
    val remainingSeconds = seconds % secondsInWorkDay
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val secs = remainingSeconds % 60
    return "${days}d:${hours}h:${minutes}m:${secs}s"
}

fun getOS(): OS {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> OS.WINDOWS
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OS.LINUX
        else -> throw IllegalStateException("Unsupported OS: $osName")
    }
}

enum class OS {
    WINDOWS, LINUX
}

fun getTotalTrackedSeconds(story: Story): Long {
    return story.trackedTimes.filter { it.endTime != null }
        .sumOf { it.endTime!!.toEpochSecond() - it.startTime.toEpochSecond() }
}

fun getTotalOffset(story: Story): Long {
    return story.offsets.sumOf { it.millisOffset / 1000 }
}

fun getTotalTrackedSecondsWithOffset(story: Story): Long {
    return getTotalTrackedSeconds(story) + getTotalOffset(story)
}

