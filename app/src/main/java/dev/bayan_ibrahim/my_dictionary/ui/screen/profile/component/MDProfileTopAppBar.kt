package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDProfileTopAppBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text("Profile") // TODO, string res
        }
    )
}

@Preview
@Composable
private fun MDProfileTopAppBarPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDProfileTopAppBar()

            }
        }
    }
}