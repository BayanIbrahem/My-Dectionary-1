package dev.bayan_ibrahim.my_dictionary.ui.screen.core.context_tag.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpOnSurface
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpSurface

@Composable
fun MDContextTagColorIcon(
    color: Color,
    isPassed: Boolean,
    canPassable: Boolean,
    modifier: Modifier = Modifier,
) {
    val surface = color.lerpSurface()
    val onSurface = color.lerpOnSurface()
    Box(
        modifier = modifier
            .size(36.dp, 20.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .padding(5.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(surface),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides onSurface
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (isPassed) {
                    MDContextTagPassedFromParentMarkerIcon()
                }
                if (canPassable) {
                    MDContextTagPassToChildrenMarkerIcon()
                }
            }
        }
    }
}

@Composable
fun MDContextTagPassedFromParentMarkerIcon(
    modifier: Modifier = Modifier,
) {
    SmallMarkerText(char = 'P', modifier = modifier,)
}

@Composable
fun MDContextTagPassToChildrenMarkerIcon(
    modifier: Modifier = Modifier,
) {
    SmallMarkerText(char = 'C', modifier = modifier,)
}

@Composable
private fun SmallMarkerText(
    char: Char,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = char.toString(),
        style = MaterialTheme.typography.labelSmall,
        fontSize = 8.sp,
        lineHeight = 7.sp,
    )
}

@Preview
@Composable
private fun MDContextTagColorIconPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MDContextTagColorIcon(color = Color.Red, isPassed = true, canPassable = true)
                    MDContextTagColorIcon(color = Color.White, isPassed = true, canPassable = false)
                    MDContextTagColorIcon(color = Color.Black, isPassed = false, canPassable = true)
                    MDContextTagColorIcon(color = Color.Green, isPassed = false, canPassable = false)
                }
            }
        }
    }
}