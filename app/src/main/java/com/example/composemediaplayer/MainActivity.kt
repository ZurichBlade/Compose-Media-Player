package com.example.composemediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.composemediaplayer.ui.main.MainScreen
import com.example.composemediaplayer.ui.theme.ComposeMediaPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMediaPlayerTheme {
                MainScreen()
            }
        }
    }
}





