package dev.bayan_ibrahim.my_dictionary.core.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.ui.MDPlainTooltip
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet

@Composable
fun MDTitleWithHint(
    title: String,
    modifier: Modifier = Modifier,
    icon: MDIconsSet? = null,
    titleHint: String? = null,
) {
    val titleContent = @Composable { modifier: Modifier ->
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let { MDIcon(it) }
            Text(title)
        }
    }
    if (titleHint == null) {
        titleContent(modifier)
    } else {
        MDPlainTooltip(
            modifier = modifier,
            tooltipContent = {
                Text(titleHint)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                titleContent(Modifier)
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(R.drawable.verify_fill), // TODO, icon res
                    contentDescription = null
                )
            }
        }
    }
}


