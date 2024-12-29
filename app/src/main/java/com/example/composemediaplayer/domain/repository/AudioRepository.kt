package com.example.composemediaplayer.domain.repository

import android.content.Context
import com.example.composemediaplayer.data.databse.AppDatabase
import com.example.composemediaplayer.data.databse.DownloadedFile
import com.example.composemediaplayer.data.model.MediaItem
import com.example.composemediaplayer.util.CommonUtils

class AudioRepository(private val context: Context) {

    private val audioFileDao = AppDatabase.getDatabase(context).audioFileDao()

    suspend fun getAllDownloadedFiles(): List<DownloadedFile> {
        return CommonUtils.getAllDownloadedFiles(context)
    }

    fun getMediaItems(): List<MediaItem> {
        return MediaRepository.getAudioFiles()
    }

    suspend fun savePlaybackProgress(filename: String, currentPosition: Long) {
        val audioFile = audioFileDao.getAudioFileByName(filename)
        audioFile?.let {
            it.progress = currentPosition
            audioFileDao.update(it)
        }
    }

    suspend fun isFileDownloaded(fileId: Int): Boolean {
        return audioFileDao.getAudioFileById(fileId) != null
    }


}
