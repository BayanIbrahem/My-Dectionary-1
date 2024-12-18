package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDAppThemeTopAppBar(
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        modifier = modifier,
        title = {
            Text("Theme") // TODO, string res
        }
    )
}
