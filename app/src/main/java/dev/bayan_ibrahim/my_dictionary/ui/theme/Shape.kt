package dev.bayan_ibrahim.my_dictionary.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.dp

val cornerSizeZero: CornerSize = CornerSize(0.dp)

val CornerBasedShape.topStartOnly: CornerBasedShape
    get() = copy(topStart, cornerSizeZero, cornerSizeZero, cornerSizeZero)

val CornerBasedShape.topEndOnly: CornerBasedShape
    get() = copy(topStart = cornerSizeZero, topEnd = topEnd, bottomStart = cornerSizeZero, bottomEnd = cornerSizeZero)

val CornerBasedShape.bottomStartOnly: CornerBasedShape
    get() = copy(topStart = cornerSizeZero, topEnd = cornerSizeZero, bottomStart = bottomStart, bottomEnd = cornerSizeZero)

val CornerBasedShape.bottomEndOnly: CornerBasedShape
    get() = copy(topStart = cornerSizeZero, topEnd = cornerSizeZero, bottomStart = cornerSizeZero, bottomEnd = bottomEnd)

val CornerBasedShape.startOnly: CornerBasedShape
    get() = copy(topStart = topStart, topEnd = cornerSizeZero, bottomStart = bottomStart, bottomEnd = cornerSizeZero)

val CornerBasedShape.endOnly: CornerBasedShape
    get() = copy(topStart = cornerSizeZero, topEnd = topEnd, bottomStart = cornerSizeZero, bottomEnd = topEnd)

val CornerBasedShape.topOnly: CornerBasedShape
    get() = copy(topStart = topStart, topEnd = topEnd, bottomStart = cornerSizeZero, bottomEnd = cornerSizeZero)

val CornerBasedShape.bottomOnly: CornerBasedShape
    get() = copy(topStart = cornerSizeZero, topEnd = cornerSizeZero, bottomStart = bottomStart, bottomEnd = bottomEnd)
