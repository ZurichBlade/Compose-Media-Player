import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults.InactiveTrackAlpha
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.composemediaplayer.R
import com.example.composemediaplayer.ui.player.MusicPlayerViewModel
import com.example.composemediaplayer.util.CommonUtils.downloadAudioFile
import com.example.composemediaplayer.util.CommonUtils.formatTime
import kotlinx.coroutines.launch

@Composable
fun MusicPlayerScreen(
    playbackPos: Long,
    tab: String,
    audioUrl: String,
    filename: String,
    onBack: () -> Unit,
    viewModel: MusicPlayerViewModel,
) {

    val context = LocalContext.current
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val currentPosition by viewModel.currentPosition.observeAsState(0L)
    val isLoading by viewModel.isLoading.observeAsState(true)
    val totalDuration by viewModel.totalDuration.observeAsState(0L)
    val coroutineScope = rememberCoroutineScope()

    // Initialize player if not already initialized
    LaunchedEffect(audioUrl) {
        viewModel.initializePlayer(audioUrl, playbackPos)
        viewModel.setPlayerListener() // Set the ExoPlayer listener
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            viewModel.updateProgress()
        }
    }

    LaunchedEffect(playbackPos) {
        if (playbackPos > 0) {
            viewModel.seekTo(playbackPos)
        }
    }


    // DisposableEffect to handle cleanup
    DisposableEffect(Unit) {
        onDispose {
//            viewModel.savePlaybackProgress(filename)
            viewModel.releasePlayer()
        }
    }


    val title = filename.replace("60.wav", "")

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = title) }, navigationIcon = {
            IconButton(onClick = {
                onBack()
            }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Album Art
                Image(
                    painter = painterResource(R.drawable.music_icon),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(30.dp))


                if (isLoading) {
                    // Show loader when loading
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                } else {
                    // Play/Pause Button
                    if (totalDuration > 0) {
                        IconButton(onClick = {
                            viewModel.togglePlayPause() // Toggle play/pause via ViewModel
                        }) {
                            Icon(
                                painter = if (isPlaying) painterResource(id = R.drawable.pause_circle_24px) else painterResource(
                                    id = R.drawable.play_circle_24px
                                ),
                                contentDescription = "Play/Pause",
                                modifier = Modifier.size(80.dp),
                                tint = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar

                        Slider(
                            value = currentPosition.toFloat(),
                            onValueChange = { newValue ->
                                viewModel.seekTo(newValue.toLong()) // Seek via ViewModel
                            },
                            valueRange = 0f..totalDuration.toFloat(),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )

                        // Current Time and Total Duration
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = formatTime(currentPosition))
                            Text(text = formatTime(totalDuration))
                        }

                        // Download button
                        if (tab == "1") {
                            DownloadButton(audioUrl = audioUrl, context = context)

                            /*   Button(
                                   onClick = {
                                       // Handle download logic here
                                       coroutineScope.launch {
                                           val downloadSuccess = downloadAudioFile(context, audioUrl)
                                           if (downloadSuccess) {
                                               Toast.makeText(
                                                   context,
                                                   "Download completed!",
                                                   Toast.LENGTH_SHORT
                                               ).show()
                                           } else {
                                               Toast.makeText(
                                                   context,
                                                   "Download failed!",
                                                   Toast.LENGTH_SHORT
                                               )
                                                   .show()
                                           }
                                       }
                                   },
                                   modifier = Modifier.padding(top = 16.dp)
                               ) {
                                   Text(text = "Download")
                               }*/
                        }

                    }
                }


            }
        }
    }

}


@Composable
fun DownloadButton(
    audioUrl: String,
    context: Context,
) {
    // Track download state
    var isDownloading by remember { mutableStateOf(false) }
    var isDownloaded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            // Prevent multiple clicks
            if (!isDownloading && !isDownloaded) {
                isDownloading = true
                coroutineScope.launch {
                    val downloadSuccess = downloadAudioFile(context, audioUrl)
                    isDownloading = false
                    isDownloaded = downloadSuccess

                    // Show a toast based on the download success
                    if (downloadSuccess) {
                        Toast.makeText(
                            context,
                            "Download completed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Download failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },
        modifier = Modifier.padding(top = 16.dp),
        enabled = !isDownloading // Disable button during download
    ) {
        if (isDownloading) {
            // Show a loading indicator while downloading
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
//                color = MaterialTheme.colors.onPrimary,
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = InactiveTrackAlpha),
                strokeWidth = 2.dp
            )
        } else {
            // Show the text based on download state
            Text(text = if (isDownloaded) "Downloaded" else "Download")
        }
    }
}



