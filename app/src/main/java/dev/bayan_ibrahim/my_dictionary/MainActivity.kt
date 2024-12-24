package dev.bayan_ibrahim.my_dictionary

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDApp
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryDynamicTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.LocalIconsPack
import java.io.InputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainActivityViewModel = hiltViewModel()
            val uiState by mainViewModel.userPreferences.collectAsStateWithLifecycle()
            if (uiState.initialized) {
                MyDictionaryDynamicTheme(
                    themeVariant = uiState.themeVariant,
                    darkColorScheme = uiState.darkColorScheme,
                    lightColorScheme = uiState.lightColorScheme
                ) {
                    CompositionLocalProvider(
                        value = LocalIconsPack provides uiState.iconsPack
                    ) {
                        MDApp()
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO, set better loading screen
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}