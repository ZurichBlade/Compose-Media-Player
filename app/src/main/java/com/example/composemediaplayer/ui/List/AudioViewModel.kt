package com.example.composemediaplayer.ui.List

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.composemediaplayer.data.databse.DownloadedFile
import com.example.composemediaplayer.data.model.MediaItem
import com.example.composemediaplayer.domain.repository.AudioRepository
import com.example.composemediaplayer.util.CommonUtils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AudioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AudioRepository(application)

    private val _remoteFiles = MutableLiveData<List<MediaItem>>()
    val remoteFiles: LiveData<List<MediaItem>> get() = _remoteFiles

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    private val _downloadedFiles = MutableLiveData<List<DownloadedFile>>()
    val downloadedFiles: LiveData<List<DownloadedFile>> get() = _downloadedFiles

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            fetchDownloadedFiles()
        }
    }

    init {
        fetchRemoteFiles()

        val intentFilter = IntentFilter("ACTION_REFRESH_LIST")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplication<Application>().registerReceiver(
                receiver, intentFilter,
                Context.RECEIVER_EXPORTED
            )
        }
    }

    private fun fetchRemoteFiles() {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)

            if (isNetworkAvailable(getApplication())) {
                val items = repository.getMediaItems()
                withContext(Dispatchers.Main) {
                    _remoteFiles.value = items
                    _loading.value = false
                }
            } else {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "No internet connection available"
                    _loading.value = false
                }
            }


        }
    }

    fun fetchDownloadedFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = repository.getAllDownloadedFiles()
            withContext(Dispatchers.Main) {
                _downloadedFiles.value = files
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(receiver)
    }
}