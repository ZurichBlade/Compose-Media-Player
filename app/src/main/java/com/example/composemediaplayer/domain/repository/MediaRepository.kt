package com.example.composemediaplayer.domain.repository

import com.example.composemediaplayer.data.model.MediaItem

object MediaRepository {
    fun getAudioFiles(): List<MediaItem> {
        return listOf(
            MediaItem(
                id = 1,
                title = "Baby Elephant Walk",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav",
            ), MediaItem(
                id = 2,
                title = "Cantina Band",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
            ), MediaItem(
                id = 3,
                title = "Imperial March",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav",
            ),
            MediaItem(
                id = 4,
                title = "Fan fare",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/Fanfare60.wav",
            ),
            MediaItem(
                id = 5,
                title = "Pink Panther",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav",
            ),
            MediaItem(
                id = 6,
                title = "Star Wars",
                audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav",
            )

        )
    }
}
