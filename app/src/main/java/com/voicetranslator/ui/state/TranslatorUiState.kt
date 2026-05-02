package com.voicetranslator.ui.state

import com.voicetranslator.data.model.Language
import com.voicetranslator.data.model.ModelDownloadState

/**
 * Represents the UI state of the translator screen
 */
data class TranslatorUiState(
    val sourceLanguage: Language = Language.ENGLISH,
    val targetLanguage: Language = Language.HINDI,
    val sourceText: String = "",
    val translatedText: String = "",
    val isTranslating: Boolean = false,
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val errorMessage: String? = null,
    val englishModelState: ModelDownloadState = ModelDownloadState.NotStarted,
    val hindiModelState: ModelDownloadState = ModelDownloadState.NotStarted,
    val isModelsReady: Boolean = false
) {
    val canTranslate: Boolean
        get() = sourceText.isNotBlank() && isModelsReady && !isTranslating

    val canSpeak: Boolean
        get() = translatedText.isNotBlank() && !isSpeaking
}
