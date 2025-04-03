package dev.bayan_ibrahim.my_dictionary.domain.model.excel

data class MDSheet(
    val languageType: MDSheetLanguageType,
    val dataType: MDSheetDataType,
    val name: String,
    val index: Int,
) {
    companion object {
        operator fun invoke(rawName: String, index: Int): MDSheet {
            val result = languageRegex.findAll(rawName).toList()
            val languageCode = result.firstOrNull()?.value?.lowercase()
            val languageType = if (languageCode == null) {
                MDSheetLanguageType.Global
            } else {
                MDSheetLanguageType.LanguageSpecific(languageCode)
            }
            val name = result.getOrNull(1)?.value?.lowercase()
            val dataType = MDSheetDataType(name)
            return MDSheet(languageType, dataType, rawName, index)
        }

        val languageRegex = "([a-zA-Z]{2,3})-(.+)".toRegex()
    }
}