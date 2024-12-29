package com.example.composemediaplayer.ui.List


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemediaplayer.R
import com.example.composemediaplayer.ui.player.MusicPlayerActivity
import com.example.composemediaplayer.util.CommonUtils.formatTime

@Composable
fun ListScreen(tabId: String) {

    val viewModel: AudioViewModel = viewModel()

    val remoteFiles by viewModel.remoteFiles.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val downloadedFiles by viewModel.downloadedFiles.observeAsState(emptyList())

    // Fetch the downloaded files when Tab 2 is selected
    if (tabId == "2" && downloadedFiles.isEmpty()) {
        viewModel.fetchDownloadedFiles()
    }

    val context = LocalContext.current

    if (tabId == "1") {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (errorMessage?.isNotEmpty() == true) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                if (remoteFiles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(remoteFiles) { mediaItem ->
                            ListItem(
                                songTitle = mediaItem.title,
                                onItemClick = {
                                    val intent =
                                        Intent(context, MusicPlayerActivity::class.java).apply {
                                            putExtra("fileId", mediaItem.id)
                                            putExtra("audioUrl", mediaItem.audioUrl)
                                            putExtra("tabNumber", tabId)
                                            putExtra("fileName", mediaItem.title)
                                        }
                                    context.startActivity(intent)
                                }
                            )
                            Divider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = Color.Gray,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }


    }

    if (tabId == "2") {

        if (downloadedFiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No downloaded files available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloadedFiles) { mediaItem ->
                    ListItem(
                        songTitle = mediaItem.fileName,
                        progress = mediaItem.progress,
                        onItemClick = {
                            // Launch MusicPlayerActivity
                            val intent = Intent(context, MusicPlayerActivity::class.java).apply {
                                putExtra("fileId", mediaItem.id)
                                putExtra("audioUrl", mediaItem.filePath)
                                putExtra("tabNumber", tabId)
                                putExtra("fileName", mediaItem.fileName)
                                putExtra("playbackPos", mediaItem.progress)
                            }
                            context.startActivity(intent)
                        })
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                }

            }
        }

    }
}

@Composable
fun ListItem(
    songTitle: String,
    progress: Long = 0,
    onItemClick: () -> Unit,
) {

    val title = songTitle.replace("60.wav", "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art
        Image(
            painter = painterResource(id = R.drawable.music_icon),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(64.dp)
                .padding(end = 8.dp)
        )

        // Song Title and Album Name
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (progress > 0) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = formatTime(progress),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

        }

    }
}
