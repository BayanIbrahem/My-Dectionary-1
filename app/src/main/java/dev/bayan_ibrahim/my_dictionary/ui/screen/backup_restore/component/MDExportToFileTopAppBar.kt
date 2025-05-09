package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportToFileTopAppBar(
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MDTopAppBar(
        modifier = modifier,
        isTopLevel = false,
        onNavigationIconClick = onNavigationIconClick,
        title = {
            Text(firstCapStringResource(R.string.export_to_file))
        }
    )

}