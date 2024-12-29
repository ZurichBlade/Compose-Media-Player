package com.example.composemediaplayer.data.databse

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_file")
data class DownloadedFile(
    @PrimaryKey val fileName: String,
    val id: Int,
    val filePath: String,
    var progress: Long = 0,
)

