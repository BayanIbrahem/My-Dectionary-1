package dev.bayan_ibrahim.my_dictionary.ui.theme.icon.icon_set

import androidx.compose.runtime.Composable
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIcon
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.R.drawable as D

@Composable
fun MDIconsSet.geDefaultIcon() = when (this) {
    MDIconsSet.AscSort -> MDIcon(D.sort) // TODO, icon res
    MDIconsSet.BarChart -> MDIcon(D.bar_chart)
    MDIconsSet.Colors -> MDIcon(D.colors1_outline, D.colors1_fill)
    MDIconsSet.CreateTime -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill)
    MDIconsSet.Delete -> MDIcon(outlinedRes = D.delete_outline, filledRes = D.delete_fill)
    MDIconsSet.DeletePermanent -> MDIcon(outlinedRes = D.delete_permanent_outline, filledRes = D.delete_permanent_fill)
    MDIconsSet.DescSort -> MDIcon(D.sort) // TODO, icon res
    MDIconsSet.ExportToFile -> MDIcon(outlinedRes = D.upload_file_outline, filledRes = D.upload_file_fill)
    MDIconsSet.Filter -> MDIcon(D.filter)
    MDIconsSet.ImportFromFile -> MDIcon(outlinedRes = D.download_file_outline, filledRes = D.download_file_fill)
    MDIconsSet.LanguageWordSpace -> MDIcon(outlinedRes = D.globe2, filledRes = D.globe1) // TODO, optimize icon
    MDIconsSet.LatestTrainsCount -> MDIcon(default = D.line_chart) // TODO, icon res
    MDIconsSet.LearningProgress -> MDIcon(outlinedRes = D.progress_40, filledRes = D.progress_60) // TODO, optimize icon (fill only)
    MDIconsSet.LineChart -> MDIcon(D.line_chart)
    MDIconsSet.Profile -> MDIcon(outlinedRes = D.profile_outline, filledRes = D.profile_fill)
    MDIconsSet.Random -> MDIcon(outlinedRes = D.random_outline, filledRes = D.random_fill)
    MDIconsSet.Reset -> MDIcon(D.reset_preferences) // TODO, icon res
    MDIconsSet.Search -> MDIcon(D.search)
    MDIconsSet.SearchFile -> MDIcon(outlinedRes = D.search_page_outline, filledRes = D.search_page_fill)
    MDIconsSet.SearchList -> MDIcon(D.search2)
    MDIconsSet.Sort -> MDIcon(default = D.sort)
    MDIconsSet.Statistics -> MDIcon(outlinedRes = D.pie_chart_outline, filledRes = D.pie_chart_fill)
    MDIconsSet.Sync -> MDIcon(default = D.sync)
    MDIconsSet.Train -> MDIcon(outlinedRes = D.excercise2_outline, filledRes = D.excersise2_fill)
    MDIconsSet.TrainHistory -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill) // TODO, optimize icon
    MDIconsSet.TrainHistoryDateGroup -> MDIcon(outlinedRes = D.date_range_outline, filledRes = D.date_range_fill)
    MDIconsSet.TrainTime -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill) // TODO, icon res
    MDIconsSet.TrainType -> MDIcon(D.excersise1) // TODO, icon res
    MDIconsSet.TrainWordsOrder -> MDIcon(D.priority)
    MDIconsSet.WordAdditionalTranslation -> MDIcon(outlinedRes = D.book5_outline, filledRes = D.book5_fill)
    MDIconsSet.WordExample -> MDIcon(outlinedRes = D.handwrite_outline, filledRes = D.handwrite_fill)
    MDIconsSet.WordMeaning -> MDIcon(outlinedRes = D.book2_outline, filledRes = D.book2_fill) // TODO, icon res
    MDIconsSet.WordRelatedWords -> MDIcon(outlinedRes = D.link_outline, filledRes = D.link_fill)
    MDIconsSet.WordTag -> MDIcon(default = D.tag)
    MDIconsSet.WordTrainSelectType -> MDIcon(default = D.select2)
    MDIconsSet.WordTrainWriteType -> MDIcon(default = D.write1_outline)
    MDIconsSet.WordTranscription -> MDIcon(outlinedRes = D.voice_out_outline, filledRes = D.voice_out_fill)
    MDIconsSet.WordTranslation -> MDIcon(outlinedRes = D.book3_outline, filledRes = D.book3_fill) // TODO, icon res
    MDIconsSet.WordTypeTag -> MDIcon(outlinedRes = D.style_outline, filledRes = D.style_fill)
    MDIconsSet.WordsList -> MDIcon(outlinedRes = D.list3_outline, filledRes = D.list3_fill)
    MDIconsSet.Add -> MDIcon(default = D.add)
    MDIconsSet.ArrowDropdown -> MDIcon(default = D.arrow_dropdown)
    MDIconsSet.ArrowForward -> MDIcon(default = D.arrow_forward)
    MDIconsSet.Check -> MDIcon(default = D.check)
    MDIconsSet.Close -> MDIcon(default = D.close)
    MDIconsSet.Edit -> MDIcon(outlinedRes = D.edit_outline, filledRes = D.edit_fill)
    MDIconsSet.Menu -> MDIcon(default = D.menu)
    MDIconsSet.MoreVert -> MDIcon(default = D.more_vert)
    MDIconsSet.Save -> MDIcon(outlinedRes = D.save_outline, filledRes = D.save_fill)
    MDIconsSet.Share -> MDIcon(outlinedRes = D.share_outline, filledRes = D.share_fill)
    MDIconsSet.Verified -> MDIcon(outlinedRes = D.verify_outline, filledRes = D.verify_fill)
}