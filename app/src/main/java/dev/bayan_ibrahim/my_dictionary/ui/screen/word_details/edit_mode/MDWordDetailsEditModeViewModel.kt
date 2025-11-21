package dev.bayan_ibrahim.my_dictionary.ui.screen.word_details.edit_mode


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.setAll
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_ID
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_LANGUAGE
import dev.bayan_ibrahim.my_dictionary.core.util.INVALID_TEXT
import dev.bayan_ibrahim.my_dictionary.domain.model.RelatedWord
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.word.WordLexicalRelationType
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordClassRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.UserPreferencesRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.WordRepo
import dev.bayan_ibrahim.my_dictionary.ui.navigate.MDDestination
import dev.bayan_ibrahim.my_dictionary.ui.screen.core.tag.MDTagsSelectorMutableUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class MDWordDetailsEditModeViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val userRepo: UserPreferencesRepo,
    private val wordClassRepo: WordClassRepo,
) : ViewModel() {
    private val _tagsState = MDTagsSelectorMutableUiState()

    private val currentLanguageFlow: StateFlow<Language?> = userRepo.getUserPreferencesStream().map { it.selectedLanguagePage }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val wordsClasses: StateFlow<List<WordClass>> = currentLanguageFlow.flatMapConcat {
        val language = it ?: INVALID_LANGUAGE
        wordClassRepo.getWordsClassesOfLanguage(language)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _uiState: MDWordDetailsEditModeMutableUiState = MDWordDetailsEditModeMutableUiState(
        availableWordsClasses = wordsClasses
    )
    val uiState: MDWordDetailsEditModeUiState = _uiState
    private var lastLoadedWord: Word? = null
    fun initWithNavArgs(args: MDDestination.WordDetailsEditMode) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.setUserPreferences {
                it.copy(
                    selectedLanguagePage = args.languageCode.code.getLanguage()
                )
            }
            val id = args.wordId
            if (id == null) {
                _uiState.reset()
            } else {
                val word = wordRepo.getWord(id)
                if (word == null) {
                    _uiState.onExecute { false }
                } else {
                    lastLoadedWord = word
                    _uiState.loadWord(word)
                }
            }
            _uiState.language = args.languageCode.code.getLanguage()
            _uiState.ensureBlankTrailingField()
        }
    }

    fun getUiActions(
        navActions: MDWordDetailsEditModeNavigationUiActions,
    ): MDWordDetailsEditModeUiActions = MDWordDetailsEditModeUiActions(
        navigationActions = navActions,
        businessActions = getBusinessUiActions(navActions)
    )

    private fun getBusinessUiActions(
        navActions: MDWordDetailsEditModeNavigationUiActions,
    ): MDWordDetailsEditModeBusinessUiActions = object : MDWordDetailsEditModeBusinessUiActions {
        override fun onAddTag(tag: Tag) {
            _uiState.tags.removeIf { it.id == tag.id }
            _uiState.tags.add(tag)
        }
        override fun onRemoveTag(tag: Tag) {
            _uiState.tags.removeIf { it.id == tag.id }
        }
        override fun onSave() {
            val lastWord = lastLoadedWord
            if (uiState.valid) {
                viewModelScope.launch {
                    _uiState.onExecute {
                        val newWord = Word(
                            meaning = uiState.meaning,
                            translation = uiState.translation,
                            language = uiState.language,
                            additionalTranslations = uiState.additionalTranslations.values.toList().filter { it.isNotBlank() },
                            tags = uiState.tags.toSet(),
                            transcription = uiState.transcription,
                            note = uiState.note,
                            examples = uiState.examples.values.toList().filter { it.isNotBlank() },
                            wordClass = uiState.selectedWordClass,
                            relatedWords = uiState.relatedWords.values.mapNotNull {
                                if (it.second.isBlank()) return@mapNotNull null
                                RelatedWord(
                                    id = INVALID_ID,
                                    baseWordId = uiState.id,
                                    relationId = it.first.id,
                                    relationLabel = it.first.label,
                                    value = it.second
                                )
                            },
                            lexicalRelations = uiState.lexicalRelations.mapValues { outer ->
                                outer.value.mapNotNull { inner ->
                                    if (inner.value.isBlank()) return@mapNotNull null
                                    WordLexicalRelation(outer.key, inner.value)
                                }
                            },
                            id = uiState.id,
                            createdAt = lastWord?.createdAt ?: Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                        val newId = wordRepo.saveOrUpdateWord(newWord)
                        launch(Dispatchers.Main.immediate) {
                            navActions.onNavigateToViewMode(
                                newWordId = newId,
                                language = uiState.language
                            )
                        }
                        true
                    }
                }
            }
        }

        override fun onEditMeaning(newMeaning: String) {
            _uiState.meaning = newMeaning
        }

        override fun onEditTranslation(newTranslation: String) {
            _uiState.translation = newTranslation
        }

        override fun onEditNote(newNote: String) {
            _uiState.note = newNote
        }

        override fun onEditTranscription(newTranscription: String) {
            _uiState.transcription = newTranscription
        }

        override fun onEditAdditionalTranslations(id: Long, newValue: String) {
            _uiState.additionalTranslations[id] = newValue
//            _uiState.filterBlankAdditionalTranslationsFields(id)
            _uiState.ensureBlankAdditionalTranslationsTrailingField()
        }

        override fun onEditExamples(id: Long, newValue: String) {
            _uiState.examples[id] = newValue
//            _uiState.filterBlankExamplesFields(id)
            _uiState.ensureBlankExamplesTrailingField()
        }

        override fun onEditWordClass(newWordClass: WordClass?) {
            _uiState.selectedWordClass = newWordClass
//            _uiState.filterBlankRelatedWordsFields()
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditTypeRelationLabel(id: Long, relation: WordClassRelation) {
            val old = _uiState.relatedWords[id]?.second ?: INVALID_TEXT
            _uiState.relatedWords[id] = Pair(relation, old)
//            _uiState.filterBlankRelatedWordsFields(id)
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditTypeRelationValue(id: Long, newValue: String) {
            val old = _uiState.relatedWords[id]?.first ?: _uiState.selectedWordClass?.relations?.firstOrNull() ?: return
            _uiState.relatedWords[id] = Pair(old, newValue)
//            _uiState.filterBlankRelatedWordsFields(id)
            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onEditLexicalRelation(type: WordLexicalRelationType, id: Long, newValue: String) {
            _uiState.lexicalRelations[type]?.put(id, newValue)
            _uiState.ensureBlankLexicalRelationsTrailingField()
        }

        override fun onFocusChange(newFocused: Long) {
            _uiState.filterBlankFields(newFocused)
            _uiState.ensureBlankTrailingField()
        }

        override fun onAdditionalTranslationsFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankAdditionalTranslationsFields(newFocused)
//            _uiState.ensureBlankAdditionalTranslationsTrailingField()
        }

        override fun onExamplesFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankExamplesFields(newFocused)
//            _uiState.ensureBlankExamplesTrailingField()
        }

        override fun onTypeRelationFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankRelatedWordsFields(newFocused)
//            _uiState.ensureBlankTypeRelationsTrailingField()
        }

        override fun onLexicalRelationFocusChange(newFocused: Long) {
            onFocusChange(newFocused)
//            _uiState.filterBlankLexicalRelationsFields(newFocused)
//            _uiState.ensureBlankLexicalRelationsTrailingField()
        }
    }
}
