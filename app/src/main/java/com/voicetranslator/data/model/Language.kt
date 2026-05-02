package com.voicetranslator.data.model

/**
 * Represents a language for translation
 */
data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String
) {
    companion object {
        val ENGLISH = Language("en", "English", "English", "🇬🇧")
        val HINDI = Language("hi", "Hindi", "हिन्दी", "🇮🇳")
        
        fun getSupportedLanguages(): List<Language> = listOf(ENGLISH, HINDI)
    }
}
