package dev.bayan_ibrahim.my_dictionary.ui.screen.marker_tags.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDMarkerTagsTopAppBar(
    onAddTag: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        modifier = modifier,
        title = {
            Text(firstCapStringResource(R.string.marker_tags))
        },
        isTopLevel = false,
        onNavigationIconClick = onNavigateBack,
        actions = {
            IconButton(
                onAddTag
            ) {
                MDIcon(MDIconsSet.Add)
            }
        }
    )
}