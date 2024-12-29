package com.example.composemediaplayer.domain.repository

import com.example.composemediaplayer.data.model.MediaItem

object MediaRepository {
    fun getAudioFiles(): List<MediaItem> {
        return listOf(
            MediaItem(
                id = 1,
                title = "Baby Elephant Walk",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
                isDownloaded = false
            ), MediaItem(
                id = 1,
                title = "Cantina Band",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
                isDownloaded = false
            ), MediaItem(
                id = 1,
                title = "Imperial March",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
                isDownloaded = false
            )

        )
    }
}
