package com.github.shahondin1624

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}
private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString())
    }
}

@Serializable
data class Story(
    val id: Int,
    val title: String,
    val trackedTimes: List<TrackedTime> = emptyList(),
    val isTracking: Boolean = false,
) {
    fun startTracking(): Story {
        logger.debug { "startTracking called for story: $title, current tracking status: $isTracking" }
        if (!isTracking) {
            val newTrackedTimes = trackedTimes.toMutableList().apply {
                add(TrackedTime(ZonedDateTime.now(), null))
            }
            logger.debug { "Story $title started tracking, trackedTimes size: ${newTrackedTimes.size}" }
            return copy(trackedTimes = newTrackedTimes, isTracking = true)
        } else {
            logger.debug { "Story $title already tracking, no action taken" }
            return this
        }
    }

    fun stopTracking(): Story {
        logger.debug { "stopTracking called for story: $title, current tracking status: $isTracking" }
        if (isTracking) {
            if (trackedTimes.isNotEmpty()) {
                logger.debug { "Updating last tracked time for story: $title, trackedTimes size: ${trackedTimes.size}" }
                val newTrackedTimes = trackedTimes.toMutableList()
                val lastIndex = newTrackedTimes.lastIndex
                newTrackedTimes[lastIndex] = TrackedTime(newTrackedTimes[lastIndex].startTime, ZonedDateTime.now())
                return copy(trackedTimes = newTrackedTimes, isTracking = false)
            } else {
                logger.debug { "No tracked times to update for story: $title" }
                return copy(isTracking = false)
            }
        } else {
            logger.debug { "Story $title not tracking, no action taken" }
            return this
        }
    }
}

@Serializable
data class TrackedTime(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val startTime: ZonedDateTime,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val endTime: ZonedDateTime?,
)
@Serializable
data class Stories(val stories: List<Story> = emptyList()) {
    fun addStory(story: Story): Stories {
        val newStories = stories.toMutableList().apply {
            add(story)
        }
        return copy(stories = newStories)
    }

    fun removeStory(story: Story): Stories {
        val newStories = stories.toMutableList().apply {
            remove(story)
        }
        return copy(stories = newStories)
    }

    fun toggleTracking(index: Int): Stories {
        logger.debug { "toggleTracking called with index: $index, stories size: ${stories.size}" }
        val newStories = stories.mapIndexed { i, story ->
            when {
                i == index -> {
                    logger.debug { "Processing story at index $i (target story)" }
                    logger.debug { "Story tracking status before: ${story.isTracking}" }
                    // Toggle the tracking state
                    val updatedStory = if (story.isTracking) {
                        story.stopTracking()
                    } else {
                        story.startTracking()
                    }
                    logger.debug { "Story tracking status after: ${updatedStory.isTracking}" }
                    updatedStory
                }

                else -> {
                    logger.debug { "Processing story at index $i (other story)" }
                    logger.debug { "Story tracking status before: ${story.isTracking}" }
                    val updatedStory = story.stopTracking()
                    logger.debug { "Story tracking status after: ${updatedStory.isTracking}" }
                    updatedStory
                }
            }
        }
        return copy(stories = newStories)
    }

    fun updateStoryTitle(index: Int, newTitle: String): Stories {
        logger.debug { "updateStoryTitle called with index: $index, new title: $newTitle" }
        val newStories = stories.mapIndexed { i, story ->
            if (i == index) {
                logger.debug { "Updating title for story at index $i from '${story.title}' to '$newTitle'" }
                story.copy(title = newTitle)
            } else {
                story
            }
        }
        return copy(stories = newStories)
    }


    fun serialize(): String {
        return json.encodeToString(this)
    }

    companion object {
        fun deserialize(string: String): Stories {
            return json.decodeFromString(string)
        }
    }
}
