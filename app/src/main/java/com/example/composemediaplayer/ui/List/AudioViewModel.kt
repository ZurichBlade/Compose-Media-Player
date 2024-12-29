package com.example.composemediaplayer.ui.List

import android.app.Application
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

    // LiveData for MediaItems (Tab 1)
    private val _remoteFiles = MutableLiveData<List<MediaItem>>()
    val remoteFiles: LiveData<List<MediaItem>> get() = _remoteFiles

    // LiveData to track the loading state
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    // LiveData to track the error state (No internet or other error)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    // LiveData for DownloadedFiles (Tab 2)
    private val _downloadedFiles = MutableLiveData<List<DownloadedFile>>()
    val downloadedFiles: LiveData<List<DownloadedFile>> get() = _downloadedFiles

    init {
        fetchRemoteFiles()
    }

    // Fetch media items for Tab 1
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
            }else{
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "No internet connection available"
                    _loading.value = false  // Stop loading
                }
            }


        }
    }

    // Fetch downloaded files for Tab 2
    fun fetchDownloadedFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = repository.getAllDownloadedFiles()
            withContext(Dispatchers.Main) {
                _downloadedFiles.value = files
            }
        }
    }
}