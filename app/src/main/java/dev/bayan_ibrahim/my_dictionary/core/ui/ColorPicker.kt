package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.calculateOutput
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.upperStringResource
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialog
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDAlertDialogActions
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicDropDownMenu
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDBasicTextField
import dev.bayan_ibrahim.my_dictionary.core.design_system.MDDialogDefaults
import dev.bayan_ibrahim.my_dictionary.core.design_system.card.vertical_card.MDCardDefaults
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.HsvColor
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.MutableHsvColor
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.blue
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.green
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.hsvColor
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpOnSurface
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.lerpSurface
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.red
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.rgbColor
import dev.bayan_ibrahim.my_dictionary.ui.theme.theme_util.strHex
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MDColorPickerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (Color) -> Unit,
    modifier: Modifier = Modifier,
    initialColor: Color = Color.Red,
    wheelHeight: Dp = 300.dp,
    horizontalAlignment: Boolean = false,
) {
    var selectedColor by remember(initialColor) {
        mutableStateOf(initialColor)
    }
    val selectedSurfaceColor = selectedColor.lerpSurface()
    val selectedOnSurfaceColor = selectedColor.lerpOnSurface()
    MDAlertDialog(
        modifier = modifier.width( IntrinsicSize.Min),
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        colors = MDDialogDefaults.colors(
            cardColors = MDCardDefaults.colors(
                headerContainerColor = selectedSurfaceColor,
                headerContentColor = selectedOnSurfaceColor,
            ),
        ),
        title = {
            Text("#" + selectedColor.toArgb().toHexString(HexFormat.UpperCase).substring(2))
        },
        actions = {
            MDAlertDialogActions(
                primaryActionLabel = firstCapStringResource(R.string.pick),
                onDismissRequest = onDismissRequest,
                onPrimaryClick = { onConfirm(selectedColor) },
            )
        }
    ) {
        MDColorPicker(
            selectedColor = selectedColor,
            onSelectColor = { selectedColor = it },
            wheelHeight = wheelHeight,
            horizontalAlignment = horizontalAlignment,
        )
    }
}

@Composable
fun MDColorPicker(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    modifier: Modifier = Modifier,
    wheelHeight: Dp = 300.dp,
    horizontalAlignment: Boolean = false,
) {
    val hsvColor by remember {
        derivedStateOf {
            MutableHsvColor(selectedColor)
        }
    }
    LaunchedEffect(selectedColor) {
        hsvColor.setFrom(selectedColor)
    }

    AlignedContainer(
        modifier = modifier.padding(8.dp),
        horizontalContainer = horizontalAlignment,
        spacedBy = 16.dp,
    ) {
        Row(
            modifier = Modifier
                .heightIn(wheelHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorPicker(
                rgbColor = selectedColor,
                hsvColor = hsvColor,
                updateColor = { hue, saturation ->
                    hsvColor.hue = hue
                    hsvColor.saturation = saturation
                    onSelectColor(hsvColor.rgbColor)
                },
                modifier = Modifier.size(wheelHeight)
            )
            MDColorLuminanceVerticalSlider(
                selectedColor = selectedColor,
                hsvColor = hsvColor,
                updateLuminance = { luminance ->
                    hsvColor.luminance = luminance
                    onSelectColor(hsvColor.rgbColor)
                },
                height = wheelHeight
            )
        }
        MDColorTextInputFieldsColumn(
            selectedColor = selectedColor,
            onSelectColor = onSelectColor,
            horizontal = horizontalAlignment,
        )
    }
}

private const val CORNER_CIRCLE_RADIUS_FACTOR = 0.17157288f // (sqrt(2) - 1) / (sqrt(2) + 1)

@Composable
private fun ColorPicker(
    rgbColor: Color,
    hsvColor: HsvColor,
    updateColor: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
    selectedColorPadding: Dp = 0.dp,
) {
    Box(
        modifier = modifier
            .pointerInputOffset { offset: Offset, size: IntSize ->
                // Center of the color wheel
                val center = size.center.toOffset()

                // Calculate the relative position from the center
                val delta = offset - center

                // Calculate the hue (angle in degrees)
                val angle = Math
                    .toDegrees(atan2(delta.y, delta.x).toDouble())
                    .toFloat()
                val hue = if (angle < 0) angle + 360 else angle

                // Calculate the distance from the center
                val distance = hypot(delta.x, delta.y)

                // Calculate the maximum radius (half of the smaller dimension)
                val minRadius = min(size.width, size.height) / 2f

                // Calculate the saturation (normalized distance from the center)
                val saturation = (distance / minRadius).coerceIn(0f, 1f)
                updateColor(hue / 360f, saturation)
            }
            .drawBehind {
                // Center of the color wheel
                val center = size.center

                // Maximum radius of the wheel (half of the smaller dimension)
                val minRadius = size.minDimension / 2
                val colorsBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red,
                    ),
                    center = center,
                )
                val saturationBrush = Brush.radialGradient(listOf(Color.White, Color.Transparent))
                drawCircle(colorsBrush)
                drawCircle(saturationBrush)

                // Angle in radians (hue is in range 0..1, map it to 0..360 degrees)
                val angle = Math.toRadians((hsvColor.hue * 360).toDouble())

                // Radial distance from the center (saturation is in range 0..1)
                val radius = hsvColor.saturation * minRadius

                // Calculate the x and y offset using polar coordinates
                val x = center.x + (radius * cos(angle)).toFloat()
                val y = center.y + (radius * sin(angle)).toFloat()
                val pointerCenter = Offset(x, y)
                drawCircle(
                    color = Color.Black,
                    radius = 9.dp.toPx(),
                    center = pointerCenter
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = pointerCenter,
                )
                // draw selected color:
                val availableRadius = CORNER_CIRCLE_RADIUS_FACTOR * minRadius
                val cornerCircleRadius = availableRadius - selectedColorPadding.toPx()
                if (cornerCircleRadius > 0f) {
                    drawCircle(
                        color = rgbColor,
                        center = Offset(cornerCircleRadius, cornerCircleRadius),
                        radius = cornerCircleRadius
                    )
                }
            }
    )
}

@Composable
private fun MDColorLuminanceVerticalSlider(
    selectedColor: Color,
    hsvColor: HsvColor,
    updateLuminance: (luminance: Float) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 24.dp,
    height: Dp = 300.dp,
) {
    val luminance by remember(hsvColor) {
        derivedStateOf {
            hsvColor.luminance
        }
    }
    val indicatorColor by remember(luminance) {
        derivedStateOf {
            if (luminance < 0.5f) Color.White else Color.Black
        }
    }
    val brush by remember(hsvColor) {
        derivedStateOf {
            val maxLuminanceColor = hsvColor.copy(luminance = 1f).rgbColor
            val minLuminanceColor = hsvColor.copy(luminance = 0f).rgbColor
            Brush.verticalGradient(colors = listOf(maxLuminanceColor, minLuminanceColor))
        }
    }

    Box(
        modifier = modifier
            .size(width, height)
            .pointerInputOffset { offset, size ->
                val heightPx = size.height.toFloat()
                val percent = calculateOutput(
                    input = offset.y,
                    inputRangeStart = 0f,
                    inputRangeEnd = heightPx,
                    outputRangeStart = 1f,
                    outputRangeEnd = 0f
                ).coerceIn(0f, 1f)
                updateLuminance(percent)
            }
            .drawBehind {
                val radius = size.width / 2
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(width.toPx()),
                    topLeft = Offset(width.toPx() / 4, 0f),
                    size = Size(width.toPx() / 2, height.toPx()),
                )
                val y = calculateOutput(
                    input = luminance,
                    inputRangeStart = 1f,
                    inputRangeEnd = 0f,
                    outputRangeStart = radius,
                    outputRangeEnd = size.height - radius,
                )

                val indicatorCenter = size.center.copy(y = y)
                drawCircle(
                    color = indicatorColor,
                    radius = (width / 2f).toPx(),
                    center = indicatorCenter
                )
            }
    )
}

/**
 * listen to tap and drag events
 */
private fun Modifier.pointerInputOffset(
    onPositionChange: (Offset, IntSize) -> Unit,
): Modifier = pointerInput(Unit) {
    coroutineScope {
        launch {
            detectTapGestures { offset ->
                onPositionChange(offset, size)
            }
        }
        launch {
            detectDragGestures { change, dragAmount ->
                onPositionChange(change.position, size)
            }
        }
    }
}

@Composable
private fun MDColorTextInputFieldsColumn(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    var selectedFormat by remember {
        mutableStateOf(ColorPickerFormat.RGB)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlignedContainer(
            horizontalContainer = !horizontal,
            verticalAlignment = Alignment.Bottom,
        ) {
            MDHexFormatContentTextFields(
                selectedColor = selectedColor,
                onSelectColor = onSelectColor,
                selectedFormat = selectedFormat,
                onSelectFormat = { selectedFormat = it },
                horizontal = horizontal,
            )
        }

        AnimatedVisibility(selectedFormat == ColorPickerFormat.HSV) {
            AlignedContainer(!horizontal) {
                MDHsvColorsTextFields(
                    selectedColor = selectedColor,
                    onSelectColor = onSelectColor,
                    horizontal = horizontal
                )
            }
        }
        AnimatedVisibility(selectedFormat == ColorPickerFormat.RGB) {
            AlignedContainer(!horizontal) {
                MDRgbColorsTextFields(
                    selectedColor = selectedColor,
                    onSelectColor = onSelectColor,
                    horizontal = horizontal
                )
            }
        }
    }
}

@Composable
private fun AlignedContainer(
    horizontalContainer: Boolean,
    spacedBy: Dp = 8.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (horizontalContainer) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacedBy, horizontalAlignment),
            verticalAlignment = verticalAlignment,
        ) {
            content()
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacedBy, verticalAlignment),
            horizontalAlignment = horizontalAlignment,
        ) {
            content()
        }
    }

}

@Composable
@OptIn(ExperimentalStdlibApi::class)
private fun MDHexFormatContentTextFields(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    selectedFormat: ColorPickerFormat,
    onSelectFormat: (ColorPickerFormat) -> Unit,
    horizontal: Boolean,
) {
    var strHex by remember {
        mutableStateOf(selectedColor.toArgb().toHexString(HexFormat.UpperCase))
    }

    var rgbHasFocus by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(selectedColor, rgbHasFocus) {
        if (!rgbHasFocus) {
            strHex = selectedColor.toArgb().toHexString(HexFormat.UpperCase)
        }
    }
    MDColorTextField(
        label = "RGB",
        value = strHex,
        onValueChange = {
            strHex = it
            onSelectColor(Color.strHex(it))
        },
        modifier = Modifier.onFocusEvent {
            rgbHasFocus = it.isFocused
        },
        leading = "#",
        horizontal = horizontal
    )
    MDColorFormatDropDown(
        format = selectedFormat,
        onSelectFormat = onSelectFormat,
    )
}

@Composable
private fun MDHsvColorsTextFields(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
) {
    val hsvColor by remember(selectedColor) {
        derivedStateOf {
            selectedColor.hsvColor
        }
    }
    MDHueColorTextField(
        hsvColor = hsvColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal
    )
    MDSaturationColorTextField(
        hsvColor = hsvColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal
    )
    MDLuminanceColorTextField(
        hsvColor = hsvColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal
    )
}

@Composable
private fun MDRgbColorsTextFields(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
) {
    MDRedColorTextField(
        selectedColor = selectedColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal,
    )
    MDGreenColorTextField(
        selectedColor = selectedColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal,
    )
    MDBlueColorTextField(
        selectedColor = selectedColor,
        onSelectColor = onSelectColor,
        horizontal = horizontal,
    )
}

@Composable
private fun MDHueColorTextField(
    hsvColor: HsvColor,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.hue),
        value = hsvColor.hue.times(360f).roundToInt().toString(),
        onValueChange = {
            it.toIntOrNull()?.let { hue ->
                onSelectColor(hsvColor.hue(hue).rgbColor)
            }
        },
        modifier = modifier,
        horizontal = horizontal
    )
}

@Composable
private fun MDSaturationColorTextField(
    hsvColor: HsvColor,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.saturation),
        value = hsvColor.saturation.times(100f).roundToInt().toString(),
        onValueChange = {
            it.toIntOrNull()?.let { saturation ->
                onSelectColor(hsvColor.saturation(saturation).rgbColor)
            }
        },
        modifier = modifier,
        horizontal = horizontal
    )
}

@Composable
private fun MDLuminanceColorTextField(
    hsvColor: HsvColor,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.luminance),
        value = hsvColor.luminance.times(100f).roundToInt().toString(),
        onValueChange = {
            it.toIntOrNull()?.let { luminance ->
                onSelectColor(hsvColor.luminance(luminance).rgbColor)
            }
        },
        modifier = modifier,
        horizontal = horizontal
    )
}

@Composable
private fun MDRedColorTextField(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.red),
        value = selectedColor.red.calculateOutput(0f, 1f, 0f, 255f).roundToInt().toString(),
        onValueChange = {
            it.ifBlank { "0" }.toIntOrNull()?.let { red ->
                onSelectColor(selectedColor.red(red))
            }
        },
        modifier = modifier,
        horizontal = horizontal
    )
}

@Composable
private fun MDGreenColorTextField(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.green),
        value = selectedColor.green.calculateOutput(0f, 1f, 0f, 255f).roundToInt().toString(),
        onValueChange = {
            it.ifBlank { "0" }.toIntOrNull()?.let { green ->
                onSelectColor(selectedColor.green(green))
            }
        },
        modifier = modifier,
        horizontal = horizontal
    )
}

@Composable
private fun MDBlueColorTextField(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    horizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    MDColorTextField(
        label = firstCapStringResource(R.string.blue),
        value = selectedColor.blue.calculateOutput(0f, 1f, 0f, 255f).roundToInt().toString(),
        onValueChange = {
            it.ifBlank { "0" }.toIntOrNull()?.let { blue ->
                onSelectColor(selectedColor.blue(blue))
            }
        },
        modifier = modifier,
        horizontal = horizontal,
    )
}

@Composable
private fun MDColorTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leading: String = "",
    placeholder: String = "",
    horizontal: Boolean,
) {
    val fieldContent = @Composable {
        MDBasicTextField(
            textStyle = MaterialTheme.typography.labelLarge,
            value = value,
            placeholder = placeholder,
            prefix = { Text(leading) },
            onValueChange = onValueChange,
            modifier = modifier.width(120.dp),
        )
    }
    val labelContent = @Composable {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
    if (horizontal) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fieldContent()
            labelContent()
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            labelContent()
            fieldContent()
        }
    }
}

@Composable
private fun MDColorFormatDropDown(
    format: ColorPickerFormat,
    onSelectFormat: (ColorPickerFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    MDBasicDropDownMenu(
        modifier = modifier.width(120.dp),
        value = format,
        allowCancelSelection = false,
        onValueChange = {},
        fieldReadOnly = true,
        textStyle = MaterialTheme.typography.labelLarge,
        suggestions = ColorPickerFormat.entries,
        suggestionTitle = { this.label },
        onSelectSuggestion = { _, f ->
            f?.let(onSelectFormat)
        }
    )
}

enum class ColorPickerFormat(@StringRes val labelRes: Int) : LabeledEnum {
    RGB(R.string.rgb), HSV(R.string.hsv);

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = upperStringResource(labelRes)
}

@Preview
@Composable
private fun MDColorPickerPreview() {
    MyDictionaryTheme() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedColor by remember {
                mutableStateOf(Color.Red)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MDColorPicker(
                    selectedColor = selectedColor,
                    onSelectColor = {
                        selectedColor = it
                    }
                )
            }
        }
    }
}