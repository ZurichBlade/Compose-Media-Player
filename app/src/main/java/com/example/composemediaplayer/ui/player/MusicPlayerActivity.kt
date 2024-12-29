package com.example.composemediaplayer.ui.player

import MusicPlayerScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.composemediaplayer.ui.player.ui.theme.ComposeMediaPlayerTheme

class MusicPlayerActivity : ComponentActivity() {

    private lateinit var viewModel: MusicPlayerViewModel
    private var tabId = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileId = intent.getIntExtra("fileId",0)
        val audioUrl = intent.getStringExtra("audioUrl") ?: ""
        tabId = intent.getStringExtra("tabNumber") ?: "1"
        val fileName = intent.getStringExtra("fileName") ?: ""
        val playbackPos = intent.getLongExtra("playbackPos", 0L)

        viewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]

        setContent {
            ComposeMediaPlayerTheme {
                MusicPlayerScreen(
                    fileId= fileId,
                    playbackPos = playbackPos,
                    filename = fileName,
                    tab = tabId,
                    audioUrl = audioUrl,
                    onBack = { finish() },
                    viewModel = viewModel
                )
            }
        }

        viewModel.initializePlayer(audioUrl, playbackPos)
        viewModel.setPlayerListener()
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

    override fun finish() {
        super.finish()
        if (tabId == "2") {
            val intent = Intent("ACTION_REFRESH_LIST")
            sendBroadcast(intent)
        }
    }

}

