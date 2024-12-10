package dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun MDLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = animationSpec,
        label = "progress indicator"
    )
    LinearProgressIndicator(
        modifier = modifier.clip(shape),
        progress = { animatedProgress },
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {}
    )
}

@Preview
@Composable
private fun MDLinearProgressIndicatorPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                var progress by remember {
                    mutableFloatStateOf(0f)
                }
                LaunchedEffect(Unit) {
                    while (true) {
                        progress = (progress + 0.25f) % 1.25f
                        delay(1.seconds)
                    }
                }
                MDLinearProgressIndicator(progress = progress)

            }
        }
    }
}