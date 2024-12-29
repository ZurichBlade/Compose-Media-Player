package com.example.composemediaplayer.domain.repository

import android.content.Context
import com.example.composemediaplayer.data.databse.AppDatabase
import com.example.composemediaplayer.data.databse.DownloadedFile
import com.example.composemediaplayer.data.model.MediaItem
import com.example.composemediaplayer.util.CommonUtils

class AudioRepository(private val context: Context) {

    private val audioFileDao = AppDatabase.getDatabase(context).audioFileDao()

    // Fetches all downloaded files from the database or local storage
    suspend fun getAllDownloadedFiles(): List<DownloadedFile> {
        return CommonUtils.getAllDownloadedFiles(context)
    }

    // Fetches all media files (MediaItem) for Tab 1
    fun getMediaItems(): List<MediaItem> {
        return MediaRepository.getAudioFiles()
    }

    // Save playback progress to the database
    suspend fun savePlaybackProgress(filename: String, currentPosition: Long) {
        val audioFile = audioFileDao.getAudioFileByName(filename)
        audioFile?.let {
            it.progress = currentPosition
            audioFileDao.update(it)
        }
    }
}
