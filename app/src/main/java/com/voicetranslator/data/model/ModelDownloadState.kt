package com.voicetranslator.data.model

/**
 * Represents the state of model download
 */
sealed class ModelDownloadState {
    object NotStarted : ModelDownloadState()
    object Downloading : ModelDownloadState()
    object Downloaded : ModelDownloadState()
    data class Error(val message: String) : ModelDownloadState()
}
