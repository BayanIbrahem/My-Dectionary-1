package dev.bayan_ibrahim.my_dictionary.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.theme.logoBlue
import dev.bayan_ibrahim.my_dictionary.ui.theme.logoGold

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    invColors: Boolean = true,
    darkColor: Color = MaterialTheme.colorScheme.logoBlue,
    lightColor: Color = MaterialTheme.colorScheme.logoGold,
) {
    val bgColor by remember(invColors, darkColor, lightColor) {
        derivedStateOf {
            if (invColors) darkColor else lightColor
        }
    }

    val fgColor by remember(invColors, darkColor, lightColor) {
        derivedStateOf {
            if (invColors) lightColor else darkColor
        }
    }

    val bgModifier by remember(bgColor) {
        derivedStateOf {
            Modifier.background(bgColor)
        }
    }
    Box(
        modifier = modifier.then(bgModifier), contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.fg_nocolor_simple),
            null,
            tint = fgColor
        )
    }
}

/**
 * return painter res of logo that we get from [getLogoRes]
 */
@Composable
fun MDIconsSet.Companion.getLogoPainter(
    /** if true then return the logo with colors */
    colorful: Boolean = true,
    /** if true then return the logo without the book marker tag in the logo use it if you want to pass specific tint
     * to the logo so it is single path now
     * */
    simple: Boolean = false,
    /**
     * if true then return the logo with dark colors ([logoBlue] if [colorful] and [Color.Black] if not)
     * and if false return the light color of the logo ([logoGold] if [colorful] and [Color.White] if not)
     * remember if [simple] is false then it would contain a book maker part which has the inverse color of the logo
     * (if the logo is dark then it is light and vise versa)
     */
    dark: Boolean = false,
): Painter = painterResource(getLogoRes(colorful, simple, dark))


fun MDIconsSet.Companion.getLogoRes(
    /** if true then return the logo with colors */
    colorful: Boolean = true,
    /** if true then return the logo without the book marker tag in the logo use it if you want to pass specific tint
     * to the logo so it is single path now
     * */
    simple: Boolean = false,
    /**
     * if true then return the logo with dark colors ([logoBlue] if [colorful] and [Color.Black] if not)
     * and if false return the light color of the logo ([logoGold] if [colorful] and [Color.White] if not)
     * remember if [simple] is false then it would contain a book maker part which has the inverse color of the logo
     * (if the logo is dark then it is light and vise versa)
     */
    dark: Boolean = false,
): Int {
    return if (simple) {
        if (colorful) {
            if (dark) {
                R.drawable.fg_simple
            } else {
                R.drawable.fg_inv_simple
            }
        } else {
            if (dark) {
                R.drawable.fg_nocolor_simple
            } else {
                R.drawable.fg_inv_nocolor_simple
            }
        }
    } else {
        if (colorful) {
            if (dark) {
                R.drawable.fg
            } else {
                R.drawable.fg_inv
            }
        } else {
            if (dark) {
                R.drawable.fg_nocolor
            } else {
                R.drawable.fg_inv_nocolor
            }
        }
    }
}

@Preview
@Composable
private fun LogoPreview() {
    MyDictionaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Logo(modifier = Modifier.size(48.dp), invColors = true)
                    }

                    item {
                        Logo(modifier = Modifier.size(48.dp), invColors = false)
                    }
                    item {
                        Logo(
                            modifier = Modifier.size(48.dp), invColors = true, darkColor = Color.Black, lightColor = Color.White
                        )
                    }

                    item {
                        Logo(
                            modifier = Modifier.size(48.dp), invColors = false, darkColor = Color.Black, lightColor = Color.White
                        )
                    }
                }
            }
        }
    }
}
