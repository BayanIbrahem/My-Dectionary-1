package dev.bayan_ibrahim.my_dictionary.domain.model.excel.table_header

sealed interface MDSheetHeader {
    val name: String
    val index: Int
    val suggestedRole: MDSheetHeaderRole?
    data class Ignored(
       override val name: String,
       override val index: Int,
       override val suggestedRole: MDSheetHeaderRole?,
    ) : MDSheetHeader

    data class Assigned(
        override val name: String,
        override val index: Int,
        override val suggestedRole: MDSheetHeaderRole?,
        val explicitRote: MDSheetHeaderRole,
    ) : MDSheetHeader
}