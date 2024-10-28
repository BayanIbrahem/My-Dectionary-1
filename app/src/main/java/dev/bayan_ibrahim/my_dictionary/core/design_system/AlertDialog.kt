package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@Composable
fun MDAlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    tertiaryActionButtonMinSpacing: Dp = 8.dp,
    shape: CornerBasedShape = MDDialogDefaults.shape,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onTertiaryClick: () -> Unit = {},
    primaryClickEnabled: Boolean = true,
    secondaryClickEnabled: Boolean = true,
    tertiaryClickEnabled: Boolean = true,
    primaryActionLabel: String = "Confirm", // TODO, string res
    secondaryActionLabel: String = "Cancel", // TODO, string res
    tertiaryActionLabel: String = "Reset", // TODO, string res
    dismissOnPrimaryClick: Boolean = true,
    dismissOnSecondaryClick: Boolean = true,
    dismissOnTertiaryClick: Boolean = true,
    primaryActionTextColor: Color = MDDialogDefaults.primaryActionColor,
    secondaryActionTextColor: Color = MDDialogDefaults.secondaryActionColor,
    tertiaryActionTextColor: Color = MDDialogDefaults.tertiaryActionColor,
    hasTertiaryAction: Boolean = false,
    showActionsHorizontalDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    MDBasicDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        modifier = modifier.width(IntrinsicSize.Max),
        shape = shape,
        title = title,
        showActionsHorizontalDivider = showActionsHorizontalDivider,
        actions = {
            AlertDialogActions(
                onDismissRequest = onDismissRequest,
                onPrimaryClick = onPrimaryClick,
                onSecondaryClick = onSecondaryClick,
                onTertiaryClick = onTertiaryClick,
                primaryClickEnabled = primaryClickEnabled,
                secondaryClickEnabled = secondaryClickEnabled,
                tertiaryClickEnabled = tertiaryClickEnabled,
                primaryActionLabel = primaryActionLabel,
                secondaryActionLabel = secondaryActionLabel,
                tertiaryActionLabel = tertiaryActionLabel,
                dismissOnPrimaryClick = dismissOnPrimaryClick,
                dismissOnSecondaryClick = dismissOnSecondaryClick,
                dismissOnTertiaryClick = dismissOnTertiaryClick,
                primaryActionTextColor = primaryActionTextColor,
                secondaryActionTextColor = secondaryActionTextColor,
                tertiaryActionTextColor = tertiaryActionTextColor,
                tertiaryButtonPadding = tertiaryActionButtonMinSpacing,
                hasTertiaryAction = hasTertiaryAction,
            )
        },
        content = content
    )

}

@Composable
fun MDAlertDialogTextTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    hasHorizontalDivider: Boolean = true,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                )
            }
        }
        if (hasHorizontalDivider) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun RowScope.AlertDialogActions(
    onDismissRequest: () -> Unit,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onTertiaryClick: () -> Unit,
    primaryClickEnabled: Boolean,
    secondaryClickEnabled: Boolean,
    tertiaryClickEnabled: Boolean,
    primaryActionLabel: String,
    secondaryActionLabel: String,
    tertiaryActionLabel: String,
    dismissOnPrimaryClick: Boolean,
    dismissOnSecondaryClick: Boolean,
    dismissOnTertiaryClick: Boolean,
    primaryActionTextColor: Color,
    secondaryActionTextColor: Color,
    tertiaryActionTextColor: Color,
    tertiaryButtonPadding: Dp,
    hasTertiaryAction: Boolean = false,
) {
    val primaryAction by remember(dismissOnPrimaryClick) {
        derivedStateOf {
            {
                if (dismissOnPrimaryClick) onDismissRequest()
                onPrimaryClick()
            }
        }
    }

    val secondaryAction by remember(dismissOnSecondaryClick) {
        derivedStateOf {
            {
                if (dismissOnSecondaryClick) onDismissRequest()
                onSecondaryClick()
            }
        }
    }

    val tertiaryAction by remember(dismissOnTertiaryClick) {
        derivedStateOf {
            {
                if (dismissOnTertiaryClick) onDismissRequest()
                onTertiaryClick()
            }
        }
    }

    if (hasTertiaryAction) {
        TextButton(
            onClick = tertiaryAction,
            enabled = tertiaryClickEnabled,
            colors = ButtonDefaults.textButtonColors(contentColor = tertiaryActionTextColor)
        ) {
            Text(tertiaryActionLabel)
        }
        Spacer(
            modifier = Modifier
                .padding(tertiaryButtonPadding)
                .weight(1f)
        )
    }
    TextButton(
        onClick = secondaryAction,
        enabled = secondaryClickEnabled,
        colors = ButtonDefaults.textButtonColors(contentColor = secondaryActionTextColor)
    ) {
        Text(secondaryActionLabel)
    }
    TextButton(
        onClick = primaryAction,
        enabled = primaryClickEnabled,
        colors = ButtonDefaults.textButtonColors(contentColor = primaryActionTextColor)
    ) {
        Text(primaryActionLabel)
    }
}

@Preview
@Composable
private fun MDAlertDialogPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MDAlertDialog(
                    showDialog = true,
                    onDismissRequest = {},
                    title = {
                        MDAlertDialogTextTitle("title", subtitle = "subtitle", hasHorizontalDivider = true)
                    },
                    onPrimaryClick = {},
                    onSecondaryClick = {},
                    hasTertiaryAction = true
                ) {
                    Text("Content")
                }
            }
        }
    }
}
