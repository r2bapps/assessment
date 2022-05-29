package com.r2b.apps.xuser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.ui.theme.XuserTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Hosts the Compose UI and initializes app-wide edge-to-edge window layout.
 *
 * Edge-to-edge support: enables drawing behind system bars and provides a full-window themed
 * surface; screen scaffolds apply safe-drawing insets to keep interactive content unobscured.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XuserTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    XuserNavigation(imageLoader = imageLoader)
                }
            }
        }
    }
}