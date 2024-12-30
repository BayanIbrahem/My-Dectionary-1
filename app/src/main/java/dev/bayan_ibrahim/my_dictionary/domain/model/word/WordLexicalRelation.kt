package dev.bayan_ibrahim.my_dictionary.domain.model.word

sealed interface WordLexicalRelation {
    val type: WordLexicalRelationType
    val relatedWord: String

    data class Synonym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Synonym
    }

    data class Antonym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Antonym
    }

    data class Hyponym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Hyponym
    }

    data class Hypernym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Hypernym
    }

    data class Meronym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Meronym
    }

    data class Holonym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Holonym
    }

    data class Homonym(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Homonym
    }

    data class Polysemy(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Polysemy
    }

    data class Prototype(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Prototype
    }

    data class Metonymy(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Metonymy
    }

    data class Collocation(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Collocation
    }

    data class Homograph(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Homograph
    }

    data class Homophone(override val relatedWord: String) : WordLexicalRelation {
        override val type: WordLexicalRelationType = WordLexicalRelationType.Homophone
    }

    companion object Companion {
        operator fun invoke(type: WordLexicalRelationType, relatedWord: String): WordLexicalRelation = when (type) {
            WordLexicalRelationType.Synonym -> Synonym(relatedWord)
            WordLexicalRelationType.Antonym -> Antonym(relatedWord)
            WordLexicalRelationType.Hyponym -> Hyponym(relatedWord)
            WordLexicalRelationType.Hypernym -> Hypernym(relatedWord)
            WordLexicalRelationType.Meronym -> Meronym(relatedWord)
            WordLexicalRelationType.Holonym -> Holonym(relatedWord)
            WordLexicalRelationType.Homonym -> Homonym(relatedWord)
            WordLexicalRelationType.Polysemy -> Polysemy(relatedWord)
            WordLexicalRelationType.Prototype -> Prototype(relatedWord)
            WordLexicalRelationType.Metonymy -> Metonymy(relatedWord)
            WordLexicalRelationType.Collocation -> Collocation(relatedWord)
            WordLexicalRelationType.Homograph -> Homograph(relatedWord)
            WordLexicalRelationType.Homophone -> Homophone(relatedWord)
        }
    }
}

/**
 * compare by type then value (null values is always the smallest values
 */
object WordLexicalRelationComparator : Comparator<WordLexicalRelation> {
    override fun compare(
        relation1: WordLexicalRelation?,
        relation2: WordLexicalRelation?,
    ): Int {
        if (relation1 == null && relation2 == null) return 0
        if (relation1 == null) return 1
        if (relation2 == null) return -1

        val type = relation1.type.compareTo(relation2.type)
        if (type != 0) return type

        return relation1.relatedWord.compareTo(relation2.relatedWord)
    }
}