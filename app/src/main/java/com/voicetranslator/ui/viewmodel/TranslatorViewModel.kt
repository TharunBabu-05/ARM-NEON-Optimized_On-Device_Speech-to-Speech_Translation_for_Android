package com.voicetranslator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetranslator.data.model.Language
import com.voicetranslator.data.model.ModelDownloadState
import com.voicetranslator.data.model.TranslationResult
import com.voicetranslator.data.repository.TranslationRepository
import com.voicetranslator.ui.state.TranslatorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the main translator screen
 */
class TranslatorViewModel : ViewModel() {

    private val repository = TranslationRepository()

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState: StateFlow<TranslatorUiState> = _uiState.asStateFlow()

    init {
        observeModelStates()
        downloadModels()
    }

    private fun observeModelStates() {
        viewModelScope.launch {
            repository.englishModelState.collect { state ->
                _uiState.update { it.copy(englishModelState = state) }
                checkModelsReady()
            }
        }
        viewModelScope.launch {
            repository.hindiModelState.collect { state ->
                _uiState.update { it.copy(hindiModelState = state) }
                checkModelsReady()
            }
        }
    }

    private fun checkModelsReady() {
        val englishReady = _uiState.value.englishModelState is ModelDownloadState.Downloaded
        val hindiReady = _uiState.value.hindiModelState is ModelDownloadState.Downloaded
        _uiState.update { it.copy(isModelsReady = englishReady && hindiReady) }
    }

    fun downloadModels() {
        viewModelScope.launch {
            repository.downloadModels()
        }
    }

    fun setSourceText(text: String) {
        _uiState.update { it.copy(sourceText = text, errorMessage = null) }
    }

    fun translate() {
        val currentState = _uiState.value
        if (currentState.sourceText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter text to translate") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTranslating = true, errorMessage = null) }
            
            val result = repository.translate(
                text = currentState.sourceText,
                sourceLanguage = currentState.sourceLanguage,
                targetLanguage = currentState.targetLanguage
            )

            when (result) {
                is TranslationResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            translatedText = result.translatedText,
                            isTranslating = false
                        )
                    }
                }
                is TranslationResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            errorMessage = result.message,
                            isTranslating = false
                        )
                    }
                }
                TranslationResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun swapLanguages() {
        _uiState.update { state ->
            state.copy(
                sourceLanguage = state.targetLanguage,
                targetLanguage = state.sourceLanguage,
                sourceText = state.translatedText,
                translatedText = state.sourceText
            )
        }
    }

    fun setListening(isListening: Boolean) {
        _uiState.update { it.copy(isListening = isListening) }
    }

    fun setSpeaking(isSpeaking: Boolean) {
        _uiState.update { it.copy(isSpeaking = isSpeaking) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearAll() {
        _uiState.update { 
            it.copy(
                sourceText = "",
                translatedText = "",
                errorMessage = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}
