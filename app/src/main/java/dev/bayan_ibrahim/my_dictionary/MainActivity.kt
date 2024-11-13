package dev.bayan_ibrahim.my_dictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDApp
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.WordDetailsRoute
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDictionaryTheme {
                MDApp()
//                WordDetailsRoute(
//                    pop = {},
//                    wordDetails = MDDestination.WordDetails(null, "en")
//                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyDictionaryTheme {
        MDApp()
    }
}
