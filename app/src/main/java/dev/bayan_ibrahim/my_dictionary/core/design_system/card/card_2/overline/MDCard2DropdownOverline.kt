package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.overline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2OverlineDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

/**
 * this overline has a clickable title
 *
 * @param titleMaxLines max lines of the subtitle, default value is [MDCard2OverlineDefaults.titleMaxLines]
 */
@Composable
fun MDCard2Overline(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    titleMaxLines: Int = MDCard2OverlineDefaults.titleMaxLines,
    onClickTitle: () -> Unit,
) {
    MDCard2Overline(
        modifier = modifier,
        leading = leading,
        trailing = trailing,
        subtitle = subtitle,
        title = {
            Row(
                modifier = Modifier
                    .heightIn(24.dp)
                    .clickable(onClick = onClickTitle),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, maxLines = titleMaxLines)
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
        },
    )
}

@Preview
@Composable
private fun MDCard2OverlinePreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDCard2Overline(
                    title = "Clickable title",
                    onClickTitle = {},
                )
            }
        }
    }
}