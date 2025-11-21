package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDSearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    colors: TextFieldColors = MDTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    ),
    leadingIcon: @Composable RowScope.() -> Unit = {
        MDIcon(
            icon = MDIconsSet.Search,
            contentDescription = null
        )
    },
    trailingIcons: @Composable RowScope.() -> Unit = {
        IconButton(
            onClick = {
                onSearchQueryChange("")
            }
        ) {
            MDIcon(MDIconsSet.Close)
        }
    },
) {
    MDBasicTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        colors = colors,
        leadingIcons = leadingIcon,
        trailingIcons = trailingIcons,
    )
}