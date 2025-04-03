package dev.bayan_ibrahim.my_dictionary.domain.model.word

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.firstCapStringResource
import dev.bayan_ibrahim.my_dictionary.ui.theme.icon.MDIconsSet
import dev.bayan_ibrahim.my_dictionary.ui.util.IconedEnum
import dev.bayan_ibrahim.my_dictionary.ui.util.LabeledEnum

enum class WordLexicalRelationType : IconedEnum, LabeledEnum {
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

    override val label: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Synonym -> firstCapStringResource(R.string.synonym)
            Antonym -> firstCapStringResource(R.string.antonym)
            Hyponym -> firstCapStringResource(R.string.hyponym)
            Hypernym -> firstCapStringResource(R.string.hypernym)
            Meronym -> firstCapStringResource(R.string.meronym)
            Holonym -> firstCapStringResource(R.string.holonym)
            Homonym -> firstCapStringResource(R.string.homonym)
            Polysemy -> firstCapStringResource(R.string.polysemy)
            Prototype -> firstCapStringResource(R.string.prototype)
            Metonymy -> firstCapStringResource(R.string.metonymy)
            Collocation -> firstCapStringResource(R.string.collocation)
            Homograph -> firstCapStringResource(R.string.homograph)
            Homophone -> firstCapStringResource(R.string.homophone)
        }
    val hint: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Synonym -> firstCapStringResource(R.string.synonym_hint)
            Antonym -> firstCapStringResource(R.string.antonym_hint)
            Hyponym -> firstCapStringResource(R.string.hyponym_hint)
            Hypernym -> firstCapStringResource(R.string.hypernym_hint)
            Meronym -> firstCapStringResource(R.string.meronym_hint)
            Holonym -> firstCapStringResource(R.string.holonym_hint)
            Homonym -> firstCapStringResource(R.string.homonym_hint)
            Polysemy -> firstCapStringResource(R.string.polysemy_hint)
            Prototype -> firstCapStringResource(R.string.prototype_hint)
            Metonymy -> firstCapStringResource(R.string.metonymy_hint)
            Collocation -> firstCapStringResource(R.string.collocation_hint)
            Homograph -> firstCapStringResource(R.string.homograph_hint)
            Homophone -> firstCapStringResource(R.string.homophone_hint)
        }

    val example: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Synonym -> firstCapStringResource(R.string.synonym_example)
            Antonym -> firstCapStringResource(R.string.antonym_example)
            Hyponym -> firstCapStringResource(R.string.hyponym_example)
            Hypernym -> firstCapStringResource(R.string.hypernym_example)
            Meronym -> firstCapStringResource(R.string.meronym_example)
            Holonym -> firstCapStringResource(R.string.holonym_example)
            Homonym -> firstCapStringResource(R.string.homonym_example)
            Polysemy -> firstCapStringResource(R.string.polysemy_example)
            Prototype -> firstCapStringResource(R.string.prototype_example)
            Metonymy -> firstCapStringResource(R.string.metonymy_example)
            Collocation -> firstCapStringResource(R.string.collocation_example)
            Homograph -> firstCapStringResource(R.string.homograph_example)
            Homophone -> firstCapStringResource(R.string.homophone_example)
        }
    val hintLikeExample: String
        @Composable
        @ReadOnlyComposable
        get() = stringResource(R.string.x_like_y, hint, example)
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
    companion object {
        fun fromKeyOrNull(key: String): WordLexicalRelationType? = entries.firstOrNull {
            it.name.equals(key, true)
        }

        fun validKey(
            key: String,
            ignoreCase: Boolean = true,
        ): Boolean = entries.any { it.name.equals(key, ignoreCase) }
    }
}