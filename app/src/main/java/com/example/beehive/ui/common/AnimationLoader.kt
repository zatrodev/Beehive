package com.example.beehive.ui.common

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AnimationLoader(
    @RawRes animationResId: Int,
    modifier: Modifier = Modifier,
) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            animationResId
        )
    )

    LottieAnimation(
        preloaderLottieComposition,
        modifier = modifier
    )
}