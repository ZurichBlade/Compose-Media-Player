package com.example.composemediaplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import com.example.composemediaplayer.data.databse.AppDatabase
import com.example.composemediaplayer.data.databse.DownloadedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object CommonUtils {

    fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    suspend fun downloadAudioFile(context: Context, fileId: Int, audioUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(audioUrl)
                val connection = url.openConnection()
                connection.connect()

                val inputStream = connection.getInputStream()
                val fileName = audioUrl.substringAfterLast("/")
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                // Save to Room Database
                val db = AppDatabase.getDatabase(context)
                db.audioFileDao()
                    .insert(
                        DownloadedFile(
                            id = fileId,
                            fileName = fileName,
                            filePath = file.absolutePath
                        )
                    )

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    suspend fun getAllDownloadedFiles(context: Context): List<DownloadedFile> {
        val db = AppDatabase.getDatabase(context)
        val audioFileDao = db.audioFileDao()
        return audioFileDao.getAllDownloadedAudio()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }


}