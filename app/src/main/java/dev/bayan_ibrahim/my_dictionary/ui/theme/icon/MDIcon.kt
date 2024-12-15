package dev.bayan_ibrahim.my_dictionary.ui.theme.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

data class MDIcon(
    @DrawableRes
    val outlinedRes: Int,
    @DrawableRes
    val filledRes: Int,
) {
    constructor(
        @DrawableRes
        default: Int,
    ) : this(default, default)

}

fun Int.asMdIcon(): MDIcon = MDIcon(this)

val MDIcon.outlinedPainter: Painter
    @Composable
    get() = painterResource(outlinedRes)

val MDIcon.filledPainter: Painter
    @Composable
    get() = painterResource(filledRes)

val MDIcon.outlinedVector: ImageVector
    @Composable
    get() = ImageVector.vectorResource(outlinedRes)

val MDIcon.filledVector: ImageVector
    @Composable
    get() = ImageVector.vectorResource(filledRes)
