package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDWordDetailsTopAppBar(
    language: Language,
    leading: @Composable BoxScope.() -> Unit,
    trailing: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = MaterialTheme.colorScheme.surfaceContainer
    val dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .height(60.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .drawBehind {
                val width = size.width.times(2)
                val left = size.width / 2

                drawRect(
                    color = bgColor,
                    size = size.copy(width = width),
                    topLeft = Offset(-left, 0f),
                )
                drawLine(
                    color = dividerColor,
                    start = Offset(-left, size.height),
                    end = Offset(size.width + left, size.height)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.align(Alignment.CenterStart), content = leading)
        Text(text = language.localDisplayName, style = MaterialTheme.typography.titleMedium)
        Box(modifier = Modifier.align(Alignment.CenterEnd), content = trailing)
    }
}

@Preview
@Composable
private fun MDWordDetailsTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDWordDetailsTopAppBar(
                    language = Language("en".code, "English", "English"),
                    leading = {},
                    trailing = {}
                )
            }
        }
    }
}