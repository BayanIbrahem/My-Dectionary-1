package dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDIcon
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDTextFieldDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDVerticalCard
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
    MDVerticalCard(
        modifier = modifier,
        headerModifier = Modifier,
        footerModifier = Modifier,
        contentModifier = Modifier,
        cardClickable = enabled,
        onClick = {
            launcher.launch(arrayOf("*/*"))
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MDIcon(MDIconsSet.ImportFromFile) // TODO, icon res
            Text(
                text = value,
                style = MDTextFieldDefaults.textStyle,
                modifier = Modifier.weight(1f),
            )
            AnimatedVisibility(type != MDFileType.Unknown) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    Text(type.typeExtensionLabel)
                }
            }
            IconButton(
                onClick = onRemove
            ) {
                MDIcon(MDIconsSet.Close)
            }
        }
    }
}

