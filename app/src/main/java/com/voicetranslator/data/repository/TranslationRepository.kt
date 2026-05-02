package com.voicetranslator.data.repository

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.voicetranslator.data.model.Language
import com.voicetranslator.data.model.ModelDownloadState
import com.voicetranslator.data.model.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Repository for handling translation operations using ML Kit
 */
class TranslationRepository {

    private var currentTranslator: Translator? = null
    private var currentSourceLang: String? = null
    private var currentTargetLang: String? = null

    private val _englishModelState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.NotStarted)
    val englishModelState: StateFlow<ModelDownloadState> = _englishModelState

    private val _hindiModelState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.NotStarted)
    val hindiModelState: StateFlow<ModelDownloadState> = _hindiModelState

    /**
     * Downloads the required translation models
     */
    suspend fun downloadModels() {
        downloadEnglishModel()
        downloadHindiModel()
    }

    private suspend fun downloadEnglishModel() {
        _englishModelState.value = ModelDownloadState.Downloading
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()
            val translator = Translation.getClient(options)
            
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            suspendCancellableCoroutine { continuation ->
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        _englishModelState.value = ModelDownloadState.Downloaded
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        _englishModelState.value = ModelDownloadState.Error(exception.message ?: "Download failed")
                        continuation.resume(Unit)
                    }
            }
            translator.close()
        } catch (e: Exception) {
            _englishModelState.value = ModelDownloadState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun downloadHindiModel() {
        _hindiModelState.value = ModelDownloadState.Downloading
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            val translator = Translation.getClient(options)
            
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            suspendCancellableCoroutine { continuation ->
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        _hindiModelState.value = ModelDownloadState.Downloaded
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        _hindiModelState.value = ModelDownloadState.Error(exception.message ?: "Download failed")
                        continuation.resume(Unit)
                    }
            }
            translator.close()
        } catch (e: Exception) {
            _hindiModelState.value = ModelDownloadState.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Translates text from source language to target language
     */
    suspend fun translate(
        text: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): TranslationResult = withContext(Dispatchers.IO) {
        if (text.isBlank()) {
            return@withContext TranslationResult.Error("Please enter text to translate")
        }

        try {
            val sourceLangCode = getMLKitLanguageCode(sourceLanguage)
            val targetLangCode = getMLKitLanguageCode(targetLanguage)

            // Create new translator if languages changed
            if (currentTranslator == null || 
                currentSourceLang != sourceLangCode || 
                currentTargetLang != targetLangCode) {
                
                currentTranslator?.close()
                
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLangCode)
                    .setTargetLanguage(targetLangCode)
                    .build()
                
                currentTranslator = Translation.getClient(options)
                currentSourceLang = sourceLangCode
                currentTargetLang = targetLangCode

                // Ensure model is downloaded
                val conditions = DownloadConditions.Builder().build()
                suspendCancellableCoroutine { continuation ->
                    currentTranslator!!.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener { continuation.resume(Unit) }
                        .addOnFailureListener { continuation.resume(Unit) }
                }
            }

            // Perform translation
            suspendCancellableCoroutine<TranslationResult> { continuation ->
                currentTranslator!!.translate(text)
                    .addOnSuccessListener { translatedText ->
                        continuation.resume(TranslationResult.Success(translatedText))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(
                            TranslationResult.Error(exception.message ?: "Translation failed")
                        )
                    }
            }
        } catch (e: Exception) {
            TranslationResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun getMLKitLanguageCode(language: Language): String {
        return when (language.code) {
            "en" -> TranslateLanguage.ENGLISH
            "hi" -> TranslateLanguage.HINDI
            else -> TranslateLanguage.ENGLISH
        }
    }

    fun close() {
        currentTranslator?.close()
        currentTranslator = null
    }
}
