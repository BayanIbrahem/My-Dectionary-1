package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDProfileTopAppBar(
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        modifier = modifier,
        isTopLevel = true,
        onNavigationIconClick = onNavigationIconClick,
        title = {
            Text(firstCapStringResource(R.string.profile))
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
                MDProfileTopAppBar({})

            }
        }
    }
}