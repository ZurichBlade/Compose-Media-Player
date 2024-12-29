package com.example.composemediaplayer.ui.player

import android.app.Application
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

    private val repository: AudioRepository = AudioRepository(context)

    init {
        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    // Initialize the player with audio URL and playback position
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

            // If playback has ended, reset to the start before playing
            if (hasEnded) {
                exoPlayer?.seekTo(0)  // Seek to the beginning (position 0)
                exoPlayer?.playWhenReady = true // Ensure playback starts immediately
                _isPlaying.value = true  // Set the playing state to true
                hasEnded = false  // Reset the hasEnded flag
            } else {
                // Toggle play/pause when it's not ended
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

    // Save playback progress to the database (or shared preferences)
    fun savePlaybackProgress(filename: String) {
        val progress = _currentPosition.value ?: 0L
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlaybackProgress(filename, progress)
        }
    }

    // Cleanup and release the player when no longer needed
    fun releasePlayer() {
        exoPlayer?.apply {
            playWhenReady = false
            release()
        }
    }

    // Set up listener to track playback state changes
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

    // Seek to a specific position
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }

}
