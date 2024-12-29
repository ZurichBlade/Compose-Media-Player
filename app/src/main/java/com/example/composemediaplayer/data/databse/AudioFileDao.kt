package com.example.composemediaplayer.data.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AudioFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(downloadedFile: DownloadedFile)

    @Query("SELECT * FROM downloaded_file")
    suspend fun getAllDownloadedAudio(): List<DownloadedFile>

    @Update
    suspend fun update(downloadedFile: DownloadedFile)

    @Query("SELECT * FROM downloaded_file WHERE fileName = :fileName LIMIT 1")
    suspend fun getAudioFileByName(fileName: String): DownloadedFile?

    @Delete
    suspend fun delete(downloadedFile: DownloadedFile)
}
