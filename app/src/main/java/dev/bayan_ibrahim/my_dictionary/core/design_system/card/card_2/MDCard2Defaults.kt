package dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.Error
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.ErrorContainer
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.ErrorOnSurface
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.Primary
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.PrimaryContainer
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.PrimaryOnSurface
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.Secondary
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SecondaryContainer
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SecondaryOnSurface
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SurfaceContainer
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SurfaceContainerHigh
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SurfaceContainerHighest
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SurfaceContainerLow
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.card_2.MDCard2ListItemTheme.SurfaceContainerLowest
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerp

object MDCard2Defaults {
    val defaultHeaderTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryContainer
    val defaultContentTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.SurfaceContainer
    val defaultFooterTheme: MDCard2ListItemTheme = MDCard2ListItemTheme.PrimaryOnSurface
    val cornerRadius = 16.dp
    val cardColor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainer
}

object MDCard2OverlineDefaults {
    const val subtitleMaxLines: Int = 1
    const val titleMaxLines: Int = 1
    val leadingMinSize: Dp = 36.dp
    val trailingMinSize: Dp = 36.dp
    val titleStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.titleMedium

    val subtitleStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelMedium

    val titleColor: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val subtitleColor: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

    val leadingColor: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

    val trailingColor: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
}

object MDCard2ListItemDefaults {
    val defaultTheme
        get() = MDCard2ListItemTheme.SurfaceContainer

    /**
     * default start padding if no leading in the list item
     */
    val noLeadingStartPadding = 16.dp
    val minHeight = 42.dp

    /** total leading size */
    val leadingSize = 36.dp

    /** leading icon content size */
    val leadingIconSize = 18.dp

    /** trailing icon content size */
    val trailingIconSize = 18.dp

    /** total trailing size */
    val trailingSize = 36.dp
    val titleMaxLines: Int = 1
    val subtitleMaxLines: Int = 1
}

object MDCard2ActionDefaults {

}

val LocalMDCard2ListItemTheme: ProvidableCompositionLocal<MDCard2ListItemTheme> = compositionLocalOf(structuralEqualityPolicy()) {
    MDCard2ListItemDefaults.defaultTheme
}


/**
 * this is the theme of the card list item
 * @property titleStyle style of title
 * @property subtitleStyle style of subtitle
 * @property titleColor color of title
 * @property subtitleColor color of subtitle
 * @property containerColor container color
 * @property leadingColor leading color
 * @property trailingColor trailing color
 * @property filledLeadingContainerColor if the leading is a filed container (like filled icon button) then this is the color of
 * the leading container, default value is [leadingColor]
 * @property filledLeadingContentColor if the leading is a filed container (like filled icon button) then this is the color of
 * the leading content, default value is [containerColor]
 * @property filledTrailingContainerColor if the trailing is a filed container (like filled icon button) then this is the color of
 * the trailing container, default value is [trailingColor]
 * @property filledTrailingContentColor if the trailing is a filed container (like filled icon button) then this is the color of
 * the trailing content, default value is [containerColor]
 * this has some variances:
 * * [Primary]
 * * [PrimaryContainer]
 * * [PrimaryOnSurface]
 * * [Secondary]
 * * [SecondaryContainer]
 * * [SecondaryOnSurface]
 * * [Error]
 * * [ErrorContainer]
 * * [ErrorOnSurface]
 * * [SurfaceDisabled]
 * * [Outline]
 * * [SurfaceContainer]
 * * [SurfaceContainerLow]
 * * [SurfaceContainerLowest]
 * * [SurfaceContainerHigh]
 * * [SurfaceContainerHighest]
 */
sealed interface MDCard2ListItemTheme {
    val titleStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelLarge

    val subtitleStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelSmall

    @get:Composable
    @get:ReadOnlyComposable
    val titleColor: Color

    @get:Composable
    @get:ReadOnlyComposable
    val subtitleColor: Color

    @get:Composable
    @get:ReadOnlyComposable
    val containerColor: Color

    @get:Composable
    @get:ReadOnlyComposable
    val leadingColor: Color
    val filledLeadingContainerColor: Color
        @Composable @ReadOnlyComposable get() = leadingColor
    val filledLeadingContentColor: Color
        @Composable @ReadOnlyComposable get() = containerColor

    @get:Composable
    @get:ReadOnlyComposable
    val trailingColor: Color
    val filledTrailingContainerColor: Color
        @Composable @ReadOnlyComposable get() = trailingColor
    val filledTrailingContentColor: Color
        @Composable @ReadOnlyComposable get() = containerColor

    data object PrimaryContainer : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
    }

    data object Primary : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    }

    data object Secondary : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
    }

    data object SecondaryContainer : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryContainer
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryContainer

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryContainer
    }

    data object SurfaceDisabled : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
    }

    data object Outline : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
    }

    data object SurfaceContainer : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainer
    }


    data object SurfaceContainerLowest : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLowest
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLowest

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLowest
    }

    data object SurfaceContainerLow : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLow
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLow

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerLow
    }


    data object SurfaceContainerHigh : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHigh
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHigh

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHigh
    }

    data object SurfaceContainerHighest : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val subtitleColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        override val containerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHighest
        override val leadingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledLeadingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledLeadingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHighest

        override val trailingColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

        override val filledTrailingContainerColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
        override val filledTrailingContentColor: Color
            @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHighest
    }

    data object Error : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError.copy(0.5f)
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError

        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error

        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError

        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error
    }

    data object ErrorContainer : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer.copy(0.5f)
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.errorContainer
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer

        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.errorContainer

        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer

        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onErrorContainer
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.errorContainer
    }

    data object PrimaryOnSurface : MDCard2ListItemTheme {

        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary.copy(0.5f)
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.surface
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary

        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onPrimary

        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary

        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.primary
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onPrimary
    }

    data object SecondaryOnSurface : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary.copy(0.5f)
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.surface
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary

        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onSecondary

        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary

        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.secondary
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onSecondary
    }

    data object ErrorOnSurface : MDCard2ListItemTheme {
        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error.copy(0.5f)
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.surface
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error

        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError

        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error

        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.error
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = MaterialTheme.colorScheme.onError
    }

    data class Custom(
        private val title: Color,
        private val subtitle: Color,
        private val container: Color,
        private val leading: Color,
        private val trailing: Color,
        private val filledLeadingContainer: Color = leading,
        private val filledLeadingContent: Color = container,
        private val filledTrailingContainer: Color = trailing,
        private val filledTrailingContent: Color = container,
        private val titleStl: TextStyle,
        private val subtitleStl: TextStyle,
    ) : MDCard2ListItemTheme {
        companion object {
            @Composable
            operator fun invoke(theme: MDCard2ListItemTheme): Custom {
                return Custom(
                    title = theme.titleColor,
                    subtitle = theme.subtitleColor,
                    container = theme.containerColor,
                    leading = theme.leadingColor,
                    trailing = theme.trailingColor,
                    filledLeadingContainer = theme.filledLeadingContainerColor,
                    filledLeadingContent = theme.filledLeadingContentColor,
                    filledTrailingContainer = theme.filledTrailingContainerColor,
                    filledTrailingContent = theme.filledTrailingContentColor,
                    titleStl = theme.titleStyle,
                    subtitleStl = theme.subtitleStyle,
                )
            }
        }

        override val titleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = title
        override val subtitleColor: Color
            @Composable
            @ReadOnlyComposable
            get() = subtitle
        override val containerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = container
        override val leadingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = leading
        override val trailingColor: Color
            @Composable
            @ReadOnlyComposable
            get() = trailing
        override val filledLeadingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = filledLeadingContent
        override val filledLeadingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = filledLeadingContainer
        override val filledTrailingContentColor: Color
            @Composable
            @ReadOnlyComposable
            get() = filledTrailingContent
        override val filledTrailingContainerColor: Color
            @Composable
            @ReadOnlyComposable
            get() = filledTrailingContainer
        override val titleStyle: TextStyle
            @Composable
            @ReadOnlyComposable
            get() = titleStl

        override val subtitleStyle: TextStyle
            @Composable
            @ReadOnlyComposable
            get() = subtitleStl

        @Composable
        override fun lerp(
            seed: Color,
            fraction: Float,
        ): Custom {
            return Custom(
                title = seed.lerp(titleColor, fraction),
                subtitle = seed.lerp(subtitleColor, fraction),
                container = seed.lerp(containerColor, fraction),
                leading = seed.lerp(leadingColor, fraction),
                trailing = seed.lerp(trailingColor, fraction),
                filledLeadingContainer = seed.lerp(filledLeadingContainerColor, fraction),
                filledLeadingContent = seed.lerp(filledLeadingContentColor, fraction),
                filledTrailingContainer = seed.lerp(filledTrailingContainerColor, fraction),
                filledTrailingContent = seed.lerp(filledTrailingContentColor, fraction),
                titleStl = titleStyle,
                subtitleStl = subtitleStyle
            )
        }
    }

    @Composable
    fun lerp(
        seed: Color,
        fraction: Float,
    ): MDCard2ListItemTheme {
        return Custom(this).lerp(seed, fraction)
    }


    /**
     * map the container to container lowest
     */
    val onSurfaceLowest: MDCard2ListItemTheme
        @Composable
        get() = Custom(this).copy(container = MaterialTheme.colorScheme.surfaceContainerLowest)

    /**
     * map the container to container low
     */
    val onSurfaceLow: MDCard2ListItemTheme
        @Composable
        get() = Custom(this).copy(container = MaterialTheme.colorScheme.surfaceContainerLow)

    /**
     * map the container to container highest
     */
    val onSurfaceHighest: MDCard2ListItemTheme
        @Composable
        get() = Custom(this).copy(container = MaterialTheme.colorScheme.surfaceContainerHighest)

    /**
     * map the container to container high
     */
    val onSurfaceHigh: MDCard2ListItemTheme
        @Composable
        get() = Custom(this).copy(container = MaterialTheme.colorScheme.surfaceContainerHigh)
}