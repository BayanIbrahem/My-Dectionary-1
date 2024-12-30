package dev.bayan_ibrahim.my_dictionary.domain.model.word

import androidx.compose.runtime.Composable
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordLexicalRelationType: IconedEnum, LabeledEnum {
    Synonym,
    Antonym,
    Hyponym,
    Hypernym,
    Meronym,
    Holonym,
    Homonym,
    Polysemy,
    Prototype,
    Metonymy,
    Collocation,
    Homograph,
    Homophone;

    override val strLabel: String
//        @Composable
        get() = when (this) {
            Synonym -> "Words with the same or very similar meanings"
            Antonym -> "Words with opposite meanings"
            Hyponym -> "A word whose meaning is included within the meaning of another"
            Hypernym -> "A word with a broader meaning that includes the meanings of other words"
            Meronym -> "Part-whole relationship"
            Holonym -> "Whole-part relationship"
            Homonym -> "Words with the same spelling and pronunciation but different meanings"
            Polysemy -> "A word with multiple related meanings"
            Prototype -> "The most typical example of a category"
            Metonymy -> "Using one word to stand for something closely related"
            Collocation -> "Frequent and natural word combinations"
            Homograph -> "Words spelled the same but with different meanings and possibly pronunciations"
            Homophone -> "Words pronounced the same but with different meanings and spellings"
        }

    val example: String
        @Composable
        get() = when (this) {
            Synonym -> "happy, joyful"
            Antonym -> "hot, cold"
            Hyponym -> "dog, animal"
            Hypernym -> "animal, dog"
            Meronym -> "wheel, car"
            Holonym -> "car, wheel"
            Homonym -> "bank (riverside), bank (financial)"
            Polysemy -> "head (body part), head (leader)"
            Prototype -> "robin (for bird)"
            Metonymy -> "The White House (for the U.S. president)"
            Collocation -> "strong tea, make a decision"
            Homograph -> "lead (metal), lead (guide)"
            Homophone -> "bear (animal), bear (tolerate)"
        }

    val relationName: String
        @Composable
        get() = when (this) {
            Synonym -> "Synonym"
            Antonym -> "Antonym"
            Hyponym -> "Hyponym"
            Hypernym -> "Hypernym"
            Meronym -> "Meronym"
            Holonym -> "Holonym"
            Homonym -> "Homonym"
            Polysemy -> "Polysemy"
            Prototype -> "Prototype"
            Metonymy -> "Metonymy"
            Collocation -> "Collocation"
            Homograph -> "Homograph"
            Homophone -> "Homophone"
        }
    override val icon: MDIconsSet
        get() = MDIconsSet.WordRelatedWords // TODO icon res
//        get() = when(this) {
//            Synonym -> TODO()
//            Antonym -> TODO()
//            Hyponym -> TODO()
//            Hypernym -> TODO()
//            Meronym -> TODO()
//            Holonym -> TODO()
//            Homonym -> TODO()
//            Polysemy -> TODO()
//            Prototype -> TODO()
//            Metonymy -> TODO()
//            Collocation -> TODO()
//            Homograph -> TODO()
//            Homophone -> TODO()
//        }
}