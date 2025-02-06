package dev.bayan_ibrahim.my_dictionary.ui.theme.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * app icons set
 * @see [currentIcon]
 */
enum class MDIconsSet {
    Add,
    ArrowDropdown,
    ArrowForward,
    ArrowUp,
    ArrowDown,
    AscSort,
    BarChart,
    Check,
    Close,
    Colors,
    CreateTime,
    Delete,
    DeletePermanent,
    DescSort,
    Edit,
    ExportToFile,
    Filter,
    ImportFromFile,
    LanguageWordSpace,
    LatestTrainsCount,
    MemorizingProbability,
    LineChart,
    Menu,
    MoreVert,
    Profile,
    Random,
    Reset,
    Save,
    Search,
    SearchFile,
    SearchList,
    Share,
    Sort,
    Statistics,
    Sync,
    Train,
    TrainHistory,
    TrainHistoryDateGroup,
    TrainTime,
    TrainType,
    TrainWordsOrder,
    Verified,
    WordAdditionalTranslation,
    WordExample,
    WordMeaning,
    WordRelatedWords,
    WordTag,
    WordTrainSelectType,
    WordTrainWriteType,
    WordTranscription,
    WordTranslation,
    WordClass,
    WordsList,
    DarkTheme,
    LightTheme,
    SystemTheme,
}

/**
 * return current icon instance according to current icon pack
 */
val MDIconsSet.currentIcon: MDIcon
    @Composable
    get() = LocalIconsPack.current.getIcon(this)

val MDIconsSet.currentOutlinedPainter: Painter
    @Composable
    get() = currentIcon.outlinedPainter

val MDIconsSet.currentFilledPainter: Painter
    @Composable
    get() = currentIcon.filledPainter

val MDIconsSet.currentOutlinedVector: ImageVector
    @Composable
    get() = currentIcon.outlinedVector

val MDIconsSet.currentFilledVector: ImageVector
    @Composable
    get() = currentIcon.filledVector
