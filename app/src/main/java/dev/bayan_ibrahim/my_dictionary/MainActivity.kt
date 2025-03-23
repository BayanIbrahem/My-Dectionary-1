package dev.bayan_ibrahim.my_dictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.FileManager
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_manager.MDAndroidFileManager
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.SharedStringsParser
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.SheetParser
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.SheetStyles
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.xml_parser.StylesParser
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDDocumentData
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileType
import dev.bayan_ibrahim.my_dictionary.ui.screen.backup_restore.component.MDFilePicker
import dev.bayan_ibrahim.my_dictionary.ui.theme.MyDictionaryDynamicTheme
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.LocalIconsPack
import kotlinx.coroutines.launch

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
                        XmlParserScreen()
//                        MDApp()
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

@Composable
fun XmlParserScreen(
    modifier: Modifier = Modifier,
    parser: SheetParser = SheetParser,
    sharedStringsParser: SharedStringsParser = SharedStringsParser,
    stylesParser: StylesParser = StylesParser,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        val context = LocalContext.current
        val fileManager: FileManager = MDAndroidFileManager(context)
        val scope = rememberCoroutineScope()

        var document: MDDocumentData? by remember {
            mutableStateOf(null)
        }
        MDFilePicker(
            data = document,
            type = MDFileType.Unknown,
            enabled = true,
            onPickFile = {
                scope.launch {
                    document = fileManager.getDocumentData(it).getOrNull()
                }
            },
            onRemove = {
                document = null
            },
        )
        val inputStream by remember(document) {
            derivedStateOf {
                document?.let { document ->
                    context.contentResolver.openInputStream(document.uri);
                }
            }
        }
//        var sheetCells: Map<SheetCellKey, SheetCell>? by remember {
//            mutableStateOf(null)
//        }
//        Column {
//            Button(
//                onClick = {
//                    val result = inputStream?.let {
//                        parser.parseSheet(it)
//                    }
//                    sheetCells = result?.getOrNull()
//                }
//            ) {
//                Text("Parse")
//            }
//        }
//        Text("parse result")
//        sheetCells?.let {
//            Text(it.values.toString())
//        }

//        var sheetSharedStrings: List<String>? by remember {
//            mutableStateOf(null)
//        }
//        Column {
//            Button(
//                onClick = {
//                    val result = inputStream?.let {
//                        sharedStringsParser.parseSharedStrings(it)
//                    }
//                    sheetSharedStrings = result?.getOrNull()
//                }
//            ) {
//                Text("Parse")
//            }
//        }
//        Text("parse result")
//        sheetSharedStrings?.let {
//            Text(it.joinToString("\n"))
//        }
//    }

        var sheetStyles: SheetStyles? by remember {
            mutableStateOf(null)
        }
        Column {
            Button(
                onClick = {
                    val result = inputStream?.let {
                        stylesParser.parseStyles(it)
                    }
                    sheetStyles = result?.getOrNull()
                }
            ) {
                Text("Parse")
            }
        }
        Text("parse result")
        sheetStyles?.let {
            Text(it.toString())
        }
    }
}
