package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.list_item.MDCard2ListItem
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDFilePicker(
    data: MDDocumentData?,
    type: MDFileType,
    enabled: Boolean,
    onPickFile: (Uri) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(onPickFile)
    }

    val value by remember(data) {
        derivedStateOf { data?.name ?: "" }
    }
    MDCard2ListItem(
        modifier = modifier,
        title = value,
        leadingIcon = {
            MDIcon(MDIconsSet.ImportFromFile) // TODO, icon res
        },
        onTrailingClick = onRemove,
        trailingIcon = {
            MDIcon(MDIconsSet.Close)
        },
        subtitle = type.takeUnless { it != MDFileType.Unknown }?.typeExtensionLabel,
        onClick = {
            if (enabled) {
                launcher.launch(arrayOf("*/*"))
            }
        }
    )
}

