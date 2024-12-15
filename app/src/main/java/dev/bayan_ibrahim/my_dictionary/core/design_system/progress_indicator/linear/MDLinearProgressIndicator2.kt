package dev.bayan_ibrahim.my_dictionary.core.design_system.progress_indicator.linear

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.default_colors.MyDictionaryTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

/**
 * @param progress the index represent the progress if [markLastProgressedItemAsDone] then the part
 * at index [progress] would be marked as progressed otherwise the last progressed item would be
 * the previous at index [progress] - 1
 * @param total total number of parts
 * @param markLastProgressedItemAsDone if true then the item with index [progress] would be treated as
 * progressed item, if true valid range is -1 until [total] (executive) and any value not in this range
 * would be coersedIn that range
 * @param gapSize space between two parts
 * @param shape shape of the single part
 */
@Composable
fun MDLinearProgressIndicator(
    progress: Int,
    total: Int,
    modifier: Modifier = Modifier,
    markLastProgressedItemAsDone: Boolean = true,
    gapSize: Dp = 2.dp,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    val progressAnimatable: Animatable<Float, AnimationVector1D> by remember {
        mutableStateOf(Animatable(progress.toFloat()))
    }
    LaunchedEffect(progress) {
        val diff = if(markLastProgressedItemAsDone) {
            1
        } else {
            0
        } + progress - progressAnimatable.value.roundToInt()

        val steps = abs(diff)
        val sign = diff.sign
        repeat(steps) {
            progressAnimatable.animateTo(progressAnimatable.value + sign)
        }
    }
    val animatedProgress by progressAnimatable.asState()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gapSize)
    ) {
        repeat(total) {
            val itemProgress by remember(animatedProgress) {
                derivedStateOf {
                    (animatedProgress.toInt() - it).coerceIn(0, 1).toFloat()
                }
            }
            MDLinearProgressIndicator(
                progress = itemProgress,
                modifier = Modifier.weight(1f),
                color = color,
                trackColor = trackColor,
                shape = shape,
                animationSpec = animationSpec
            )
        }
    }
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
                val total = 10
                var progress by remember {
                    mutableIntStateOf(0)
                }
                LaunchedEffect(Unit) {
                    while (true) {
                        progress = Random.nextInt(0, total.inc())
                        delay(3.seconds)
                    }
                }
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(progress.toString())

                    MDLinearProgressIndicator(
                        progress = progress,
                        total = total
                    )
                }
            }
        }
    }
}