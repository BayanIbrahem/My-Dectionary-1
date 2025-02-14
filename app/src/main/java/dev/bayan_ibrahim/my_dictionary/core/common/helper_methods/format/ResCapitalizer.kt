package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

@Composable
@ReadOnlyComposable
fun capitalizeStringRes(
    @StringRes res: Int,
    capitalizer: StringCapitalizer,
    vararg args: Any,
): String {
    return capitalizeString(stringResource(res, args), capitalizer)
}

@Composable
@ReadOnlyComposable
@JvmName("stringCapitalizerApplyOnStringRes")
fun StringCapitalizer.applyOn(
    @StringRes string: Int,
    vararg args: Any,
) = capitalizeStringRes(string, this, args)

@Composable
@ReadOnlyComposable
fun capitalizePluralsRes(
    @PluralsRes res: Int,
    quantity: Int,
    capitalizer: StringCapitalizer,
    vararg args: Any,
): String {
    return capitalizeString(pluralStringResource(res, quantity, args), capitalizer)
}

@Composable
@ReadOnlyComposable
@JvmName("stringCapitalizerApplyOnPluralsRes")
fun StringCapitalizer.applyOn(
    @PluralsRes string: Int,
    quantity: Int,
    vararg args: Any,
) = capitalizePluralsRes(string, quantity, this, args)

@Composable
@ReadOnlyComposable
fun upperStringResource(
    @StringRes res: Int,
    vararg args: Any,
) = StringCapitalizer.ALL_CAPS.applyOn(res, args)

@Composable
@ReadOnlyComposable
fun lowerStringResource(
    @StringRes res: Int,
    vararg args: Any,
) = StringCapitalizer.NONE_CAPS.applyOn(res, args)

@Composable
@ReadOnlyComposable
fun firstCapStringResource(
    @StringRes res: Int,
    vararg args: Any,
) = StringCapitalizer.FIRST_CHAR.applyOn(res, args)

@Composable
@ReadOnlyComposable
fun eachFirstCapStringResource(
    @StringRes res: Int,
    vararg args: Any,
) = StringCapitalizer.EACH_FIRST_CHAR.applyOn(res, args)

@Composable
@ReadOnlyComposable
fun upperPluralsResource(
    @PluralsRes res: Int,
    quantity: Int,
    vararg args: Any,
) = StringCapitalizer.ALL_CAPS.applyOn(res, quantity, args)

@Composable
@ReadOnlyComposable
fun lowerPluralsResource(
    @PluralsRes res: Int,
    quantity: Int,
    vararg args: Any,
) = StringCapitalizer.NONE_CAPS.applyOn(res, quantity, args)

@Composable
@ReadOnlyComposable
fun firstCapPluralsResource(
    @PluralsRes res: Int,
    quantity: Int,
    vararg args: Any,
) = StringCapitalizer.FIRST_CHAR.applyOn(res, quantity, args)

@Composable
@ReadOnlyComposable
fun eachFirstCapPluralsResource(
    @PluralsRes res: Int,
    quantity: Int,
    vararg args: Any,
) = StringCapitalizer.EACH_FIRST_CHAR.applyOn(res, quantity, args)
