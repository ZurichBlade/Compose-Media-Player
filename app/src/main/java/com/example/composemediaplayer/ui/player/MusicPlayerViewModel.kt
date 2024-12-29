package com.example.composemediaplayer.ui.player

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.composemediaplayer.domain.repository.AudioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _isLoading = MutableLiveData<Boolean>(true) // Track loading state
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentPosition = MutableLiveData<Long>(0L)
    val currentPosition: LiveData<Long> get() = _currentPosition

    private val _totalDuration = MutableLiveData<Long>(0L)
    val totalDuration: LiveData<Long> get() = _totalDuration

    private var exoPlayer: ExoPlayer? = null
    private var hasEnded = false

    private val _isFileDownloaded = mutableStateOf(false)
    val isFileDownloaded: State<Boolean> get() = _isFileDownloaded

    private val repository: AudioRepository = AudioRepository(context)

    init {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun initializePlayer(audioUrl: String, initialPosition: Long) {
        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            seekTo(initialPosition)
            playWhenReady = false
        }
        _isLoading.value = true
    }

    // Toggle play/pause
    fun togglePlayPause() {
        CoroutineScope(Dispatchers.Main).launch {
            val isCurrentlyPlaying = exoPlayer?.playWhenReady == true

            if (hasEnded) {
                exoPlayer?.seekTo(0)
                exoPlayer?.playWhenReady = true
                _isPlaying.value = true
                hasEnded = false
            } else {
                exoPlayer?.playWhenReady = !isCurrentlyPlaying
                _isPlaying.value = !isCurrentlyPlaying
            }
        }
    }

    // Update playback position
    fun updateProgress() {
        CoroutineScope(Dispatchers.Main).launch {
            while (_isPlaying.value == true) {
                _currentPosition.postValue(exoPlayer?.currentPosition ?: 0L)
                delay(500)
            }
        }
    }

    fun savePlaybackProgress(filename: String) {
        val progress = _currentPosition.value ?: 0L
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlaybackProgress(filename, progress)
        }
    }

    fun checkIfFileDownloaded(fileId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isFileDownloaded.value = repository.isFileDownloaded(fileId)
        }
    }

    fun releasePlayer() {
        exoPlayer?.apply {
            playWhenReady = false
            release()
        }
    }

    fun setPlayerListener() {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    _totalDuration.value = exoPlayer?.duration ?: 0L
                    _isLoading.value = false
                }
                if (state == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    hasEnded = true
                }
            }
        })
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }

}
