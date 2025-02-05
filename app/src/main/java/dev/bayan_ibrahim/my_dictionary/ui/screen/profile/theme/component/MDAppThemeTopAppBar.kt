package dev.bayan_ibrahim.my_dictionary.ui.screen.profile.theme.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDAppThemeTopAppBar(
    isDark: Boolean?,
    onNavigationIconClick: () -> Unit,
    onToggleDarkVariance: (isDark: Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        modifier = modifier,
        isTopLevel = false,
        onNavigationIconClick = onNavigationIconClick,
        title = {
            Text("Theme") // TODO, string res
        },
        actions = {
            val iconRes by remember(isDark) {
                derivedStateOf {
                    when(isDark){
                        true -> MDIconsSet.DarkTheme
                        false -> MDIconsSet.LightTheme
                        null -> MDIconsSet.SystemTheme
                    }
                }
            }
            IconButton(
                onClick = {
                    onToggleDarkVariance(
                        when(isDark) {
                            true -> false
                            false -> null
                            null -> true
                        }
                    )
                }
            ) {
                MDIcon(iconRes)
            }
        }

    )
}
