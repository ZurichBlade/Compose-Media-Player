package com.example.composemediaplayer.ui.player

import MusicPlayerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.composemediaplayer.ui.player.ui.theme.ComposeMediaPlayerTheme

class MusicPlayerActivity : ComponentActivity() {

    private lateinit var viewModel: MusicPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioUrl = intent.getStringExtra("audioUrl") ?: ""
        val tab = intent.getStringExtra("tabNumber") ?: "1"
        val fileName = intent.getStringExtra("fileName") ?: ""
        val playbackPos = intent.getLongExtra("playbackPos", 0L)

        viewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]

        setContent {
            ComposeMediaPlayerTheme {
                MusicPlayerScreen(
                    playbackPos = playbackPos,
                    filename = fileName,
                    tab = tab,
                    audioUrl = audioUrl,
                    onBack = { finish() },
                    viewModel = viewModel
                )
            }
        }

        // Initialize the player in the ViewModel
        viewModel.initializePlayer(audioUrl, playbackPos)
        viewModel.setPlayerListener() // Set the ExoPlayer listener
    }

    override fun onPause() {
        super.onPause()
        val filename = intent.getStringExtra("fileName") ?: ""
        viewModel.savePlaybackProgress(filename)
    }

    override fun onStop() {
        super.onStop()
        viewModel.releasePlayer()
    }

}

