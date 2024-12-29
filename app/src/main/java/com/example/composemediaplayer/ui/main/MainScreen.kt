package com.example.composemediaplayer.ui.main


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composemediaplayer.ui.List.ListScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val selectedTab = remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab.intValue,
                onTabSelected = { selectedTab.intValue = it }
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "screen_1"
            ) {
                // List Screen for Tab 1
                composable("screen_1") {
                    ListScreen(
                        tabId = "1",
                    )
                }

                // List Screen for Tab 2
                composable("screen_2") {
                    ListScreen(
                        tabId = "2",
                    )
                }

            }
        }

        // Update the current tab based on selectedTab
        LaunchedEffect(selectedTab.intValue) {
            navController.navigate(
                when (selectedTab.intValue) {
                    0 -> "screen_1"
                    1 -> "screen_2"
                    else -> "screen_1"
                }
            ) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}

