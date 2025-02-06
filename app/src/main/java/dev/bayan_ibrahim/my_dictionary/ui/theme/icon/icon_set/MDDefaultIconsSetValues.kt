package dev.bayan_ibrahim.my_dictionary.ui.theme.icon.icon_set

import androidx.compose.runtime.Composable
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIcon
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.R.drawable as D
import dev.bayan_ibrahim.my_dictionary.R.string as S

@Composable
fun MDIconsSet.getDefaultIcon() = when (this) {
    MDIconsSet.AscSort -> MDIcon(default = D.sort, contentDescription = S.content_description_asc_sort) // TODO, icon res
    MDIconsSet.BarChart -> MDIcon(default = D.bar_chart, contentDescription = S.content_description_bar_chart)
    MDIconsSet.Colors -> MDIcon(outlinedRes = D.colors1_outline, filledRes = D.colors1_fill, contentDescription = S.content_description_colors)
    MDIconsSet.CreateTime -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill, contentDescription = S.content_description_create_time)
    MDIconsSet.Delete -> MDIcon(outlinedRes = D.delete_outline, filledRes = D.delete_fill, contentDescription = S.content_description_delete)
    MDIconsSet.DeletePermanent -> MDIcon(outlinedRes = D.delete_permanent_outline, filledRes = D.delete_permanent_fill, contentDescription = S.content_description_delete_permanent)
    MDIconsSet.DescSort -> MDIcon(default = D.sort, contentDescription = S.content_description_desc_sort) // TODO, icon res
    MDIconsSet.ExportToFile -> MDIcon(outlinedRes = D.upload_file_outline, filledRes = D.upload_file_fill, contentDescription = S.content_description_export_to_file)
    MDIconsSet.Filter -> MDIcon(default = D.filter, contentDescription = S.content_description_filter)
    MDIconsSet.ImportFromFile -> MDIcon(outlinedRes = D.download_file_outline, filledRes = D.download_file_fill, contentDescription = S.content_description_import_from_file)
    MDIconsSet.LanguageWordSpace -> MDIcon(outlinedRes = D.globe2, filledRes = D.globe1, contentDescription = S.content_description_language_word_space) // TODO, optimize icon
    MDIconsSet.LatestTrainsCount -> MDIcon(default = D.line_chart, contentDescription = S.content_description_latest_trains_count) // TODO, icon res
    MDIconsSet.MemorizingProbability -> MDIcon(outlinedRes = D.progress_40, filledRes = D.progress_60, contentDescription = S.content_description_learning_progress) // TODO, optimize icon (fill only)
    MDIconsSet.LineChart -> MDIcon(default = D.line_chart, contentDescription = S.content_description_line_chart)
    MDIconsSet.Profile -> MDIcon(outlinedRes = D.profile_outline, filledRes = D.profile_fill, contentDescription = S.content_description_profile)
    MDIconsSet.Random -> MDIcon(outlinedRes = D.random_outline, filledRes = D.random_fill, contentDescription = S.content_description_random)
    MDIconsSet.Reset -> MDIcon(default = D.reset_preferences, contentDescription = S.content_description_reset) // TODO, icon res
    MDIconsSet.Search -> MDIcon(default = D.search, contentDescription = S.content_description_search)
    MDIconsSet.SearchFile -> MDIcon(outlinedRes = D.search_page_outline, filledRes = D.search_page_fill, contentDescription = S.content_description_search_file)
    MDIconsSet.SearchList -> MDIcon(default = D.search2, contentDescription = S.content_description_search_list)
    MDIconsSet.Sort -> MDIcon(default = D.sort, contentDescription = S.content_description_sort)
    MDIconsSet.Statistics -> MDIcon(outlinedRes = D.pie_chart_outline, filledRes = D.pie_chart_fill, contentDescription = S.content_description_statistics)
    MDIconsSet.Sync -> MDIcon(default = D.sync, contentDescription = S.content_description_sync)
    MDIconsSet.Train -> MDIcon(outlinedRes = D.excercise2_outline, filledRes = D.excersise2_fill, contentDescription = S.content_description_train)
    MDIconsSet.TrainHistory -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill, contentDescription = S.content_description_train_history) // TODO, optimize icon
    MDIconsSet.TrainHistoryDateGroup -> MDIcon(outlinedRes = D.date_range_outline, filledRes = D.date_range_fill, contentDescription = S.content_description_train_history_date_group)
    MDIconsSet.TrainTime -> MDIcon(outlinedRes = D.time_outline, filledRes = D.time_fill, contentDescription = S.content_description_train_time) // TODO, icon res
    MDIconsSet.TrainType -> MDIcon(default = D.excersise1, contentDescription = S.content_description_train_type) // TODO, icon res
    MDIconsSet.TrainWordsOrder -> MDIcon(default = D.priority, contentDescription = S.content_description_train_words_order)
    MDIconsSet.WordAdditionalTranslation -> MDIcon(outlinedRes = D.book5_outline, filledRes = D.book5_fill, contentDescription = S.content_description_word_additional_translation)
    MDIconsSet.WordExample -> MDIcon(outlinedRes = D.handwrite_outline, filledRes = D.handwrite_fill, contentDescription = S.content_description_word_example)
    MDIconsSet.WordMeaning -> MDIcon(outlinedRes = D.book2_outline, filledRes = D.book2_fill, contentDescription = S.content_description_word_meaning) // TODO, icon res
    MDIconsSet.WordRelatedWords -> MDIcon(outlinedRes = D.link_outline, filledRes = D.link_fill, contentDescription = S.content_description_word_related_words)
    MDIconsSet.WordTag -> MDIcon(default = D.tag, contentDescription = S.content_description_word_tag)
    MDIconsSet.WordTrainSelectType -> MDIcon(default = D.select2, contentDescription = S.content_description_word_train_select_type)
    MDIconsSet.WordTrainWriteType -> MDIcon(default = D.write1_outline, contentDescription = S.content_description_word_train_write_type)
    MDIconsSet.WordTranscription -> MDIcon(outlinedRes = D.voice_out_outline, filledRes = D.voice_out_fill, contentDescription = S.content_description_word_transcription)
    MDIconsSet.WordTranslation -> MDIcon(outlinedRes = D.book3_outline, filledRes = D.book3_fill, contentDescription = S.content_description_word_translation) // TODO, icon res
    MDIconsSet.WordClass -> MDIcon(outlinedRes = D.style_outline, filledRes = D.style_fill, contentDescription = S.content_description_word_word_class)
    MDIconsSet.WordsList -> MDIcon(outlinedRes = D.list3_outline, filledRes = D.list3_fill, contentDescription = S.content_description_words_list)
    MDIconsSet.Add -> MDIcon(default = D.add, contentDescription = S.content_description_add)
    MDIconsSet.ArrowDropdown -> MDIcon(default = D.arrow_dropdown, contentDescription = S.content_description_arrow_dropdown)
    MDIconsSet.ArrowForward -> MDIcon(default = D.arrow_forward, contentDescription = S.content_description_arrow_forward)
    MDIconsSet.ArrowUp -> MDIcon(default = D.arrow_upward, contentDescription = S.content_description_arrow_up)
    MDIconsSet.ArrowDown -> MDIcon(default = D.arrow_downward, contentDescription = S.content_description_arrow_down)
    MDIconsSet.Check -> MDIcon(default = D.check, contentDescription = S.content_description_check)
    MDIconsSet.Close -> MDIcon(default = D.close, contentDescription = S.content_description_close)
    MDIconsSet.Edit -> MDIcon(outlinedRes = D.edit_outline, filledRes = D.edit_fill, contentDescription = S.content_description_edit)
    MDIconsSet.Menu -> MDIcon(default = D.menu, contentDescription = S.content_description_menu)
    MDIconsSet.MoreVert -> MDIcon(default = D.more_vert, contentDescription = S.content_description_more_vert)
    MDIconsSet.Save -> MDIcon(outlinedRes = D.save_outline, filledRes = D.save_fill, contentDescription = S.content_description_save)
    MDIconsSet.Share -> MDIcon(outlinedRes = D.share_outline, filledRes = D.share_fill, contentDescription = S.content_description_share)
    MDIconsSet.Verified -> MDIcon(outlinedRes = D.verify_outline, filledRes = D.verify_fill, contentDescription = S.content_description_verified)
    MDIconsSet.DarkTheme -> MDIcon(outlinedRes = D.dark_mode_outline, filledRes = D.dark_mode_fill, contentDescription = S.content_description_dark_theme)
    MDIconsSet.LightTheme -> MDIcon(outlinedRes = D.light_mode_outline, filledRes = D.dark_mode_fill, contentDescription = S.content_description_light_theme)
    MDIconsSet.SystemTheme -> MDIcon(outlinedRes = D.contrast, filledRes = D.contrast, contentDescription = S.content_description_system_theme)
    MDIconsSet.Ltr-> MDIcon(outlinedRes = D.ltr, filledRes = D.ltr, contentDescription = S.content_description_ltr)
    MDIconsSet.Rtl-> MDIcon(outlinedRes = D.rtl, filledRes = D.rtl, contentDescription = S.content_description_rtl)
    MDIconsSet.DeviceDirection-> MDIcon(outlinedRes = D.device_direction, filledRes = D.device_direction, contentDescription = S.content_description_device_direction)
}
