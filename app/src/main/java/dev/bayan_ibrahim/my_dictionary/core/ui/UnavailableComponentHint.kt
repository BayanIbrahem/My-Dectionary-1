package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun UnavailableComponentHint(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) // TODO, string res
}

@Preview
@Composable
private fun UnavailableComponentHintPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier) {
                UnavailableComponentHint("Unavailable text hint")
            }
        }
    }
}