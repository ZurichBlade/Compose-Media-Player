package com.example.composemediaplayer.ui.main


import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.composemediaplayer.R

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    BottomNavigation {
        BottomNavigationItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Home,
                    contentDescription = "Tab 1"
                )
            },
            label = { Text("Home") }
        )
        BottomNavigationItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.download_for_offline_24px),
                    contentDescription = "Tab 2"
                )
            },
            label = { Text("Downloads") }
        )

    }
}
