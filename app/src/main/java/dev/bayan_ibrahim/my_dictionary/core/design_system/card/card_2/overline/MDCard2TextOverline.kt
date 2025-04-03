package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2OverlineDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * like normal generic overline but with text [title] and text [subtitle] instead of general composable
 * @param titleMaxLines max lines of the title, default value is [MDCard2OverlineDefaults.titleMaxLines]
 * @param subtitleMaxLines max lines of the subtitle, default value is [MDCard2OverlineDefaults.subtitleMaxLines]
 * @see MDCard2Overline
 */
@Composable
fun MDCard2Overline(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    subtitleMaxLines: Int = MDCard2OverlineDefaults.subtitleMaxLines,
    titleMaxLines: Int = MDCard2OverlineDefaults.titleMaxLines,
) {
    MDCard2Overline(
        modifier = modifier,
        leading = leading,
        trailing = trailing,
        subtitle = subtitle?.let { { Text(subtitle, maxLines = subtitleMaxLines) } }
    ) {
        Text(title, maxLines = titleMaxLines)
    }
}

@Preview
@Composable
private fun MDCard2OverlinePreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MDCard2Overline(
                    leading = {
                        Icon(Icons.Default.Call, null)
                    },
                    trailing = {
                        Icon(Icons.Default.Close, null)
                    },
                    subtitle = "some subtitle",
                    title = "Overline with leading, trailing and subtitle"
                )
                MDCard2Overline(
                    leading = null,
                    trailing = {
                        Icon(Icons.Default.Close, null)
                    },
                    title = "Overline with leading, trailing and subtitle"
                )
                MDCard2Overline(
                    subtitle = "some subtitle",
                    title = "Overline with leading, trailing and subtitle"
                )
                MDCard2Overline(title = "Overline")
            }
        }
    }
}
