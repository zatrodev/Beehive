package com.example.beehive.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.beehive.R
import com.example.beehive.ui.Dimensions.LoadingIconSize

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        Box {
            AnimationLoader(
                animationResId = R.raw.loading_bee,
                modifier = Modifier
                    .size(LoadingIconSize)
                    .align(Alignment.Center)

            )
        }
    }
}