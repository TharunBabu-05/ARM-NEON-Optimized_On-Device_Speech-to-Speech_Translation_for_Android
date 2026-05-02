package com.voicetranslator.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.voicetranslator.R
import com.voicetranslator.data.model.Language
import com.voicetranslator.data.model.ModelDownloadState
import com.voicetranslator.databinding.ActivityMainBinding
import com.voicetranslator.ui.viewmodel.TranslatorViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TranslatorViewModel by viewModels()
    
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceInput()
        } else {
            showSnackbar(getString(R.string.permission_denied))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeTextToSpeech()
        initializeSpeechRecognizer()
        setupUI()
        observeState()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
        }
    }

    private fun setupUI() {
        // Source text input
        binding.etSourceText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                viewModel.setSourceText(text)
                binding.tvCharCount.text = "${text.length}/5000"
                binding.btnClearSource.isVisible = text.isNotEmpty()
            }
        })

        // Clear source text
        binding.btnClearSource.setOnClickListener {
            binding.etSourceText.text?.clear()
            viewModel.clearAll()
        }

        // Voice input
        binding.btnVoiceInput.setOnClickListener {
            checkPermissionAndStartVoiceInput()
        }

        // Translate button
        binding.btnTranslate.setOnClickListener {
            viewModel.translate()
        }

        // Swap languages
        binding.btnSwapLanguages.setOnClickListener {
            viewModel.swapLanguages()
            animateSwap()
        }

        // Speak translation
        binding.btnSpeakTranslation.setOnClickListener {
            speakTranslation()
        }

        // Copy translation
        binding.btnCopyTranslation.setOnClickListener {
            copyToClipboard()
        }

        // Share translation
        binding.btnShareTranslation.setOnClickListener {
            shareTranslation()
        }

        // Stop listening
        binding.btnStopListening.setOnClickListener {
            stopVoiceInput()
        }

        // Voice overlay dismiss
        binding.overlayVoiceInput.setOnClickListener {
            stopVoiceInput()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: com.voicetranslator.ui.state.TranslatorUiState) {
        // Update language labels
        binding.tvSourceLabel.text = state.sourceLanguage.name
        binding.tvTargetLabel.text = state.targetLanguage.name
        binding.btnSourceLanguage.text = "${state.sourceLanguage.flag} ${state.sourceLanguage.name}"
        binding.btnTargetLanguage.text = "${state.targetLanguage.flag} ${state.targetLanguage.name}"

        // Update translation
        binding.tvTranslatedText.text = if (state.translatedText.isNotEmpty()) {
            state.translatedText
        } else {
            getString(R.string.hint_translation_appear)
        }

        // Loading state
        binding.progressTranslation.isVisible = state.isTranslating
        binding.tvTranslatedText.isVisible = !state.isTranslating

        // Translate button state
        binding.btnTranslate.isEnabled = state.canTranslate

        // Model download status
        val isDownloading = state.englishModelState is ModelDownloadState.Downloading ||
                state.hindiModelState is ModelDownloadState.Downloading
        binding.cardModelStatus.isVisible = isDownloading

        // Voice input overlay
        binding.overlayVoiceInput.isVisible = state.isListening

        // Speaking state
        if (state.isSpeaking) {
            binding.btnSpeakTranslation.setIconResource(R.drawable.ic_stop)
        } else {
            binding.btnSpeakTranslation.setIconResource(R.drawable.ic_volume)
        }

        // Error handling
        state.errorMessage?.let { message ->
            showSnackbar(message)
            viewModel.clearError()
        }
    }

    private fun checkPermissionAndStartVoiceInput() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceInput() {
        val currentState = viewModel.uiState.value
        val locale = if (currentState.sourceLanguage == Language.HINDI) {
            Locale("hi", "IN")
        } else {
            Locale.ENGLISH
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        viewModel.setListening(true)
        speechRecognizer?.startListening(intent)
    }

    private fun stopVoiceInput() {
        speechRecognizer?.stopListening()
        viewModel.setListening(false)
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.tvListeningStatus.text = getString(R.string.listening)
            }

            override fun onBeginningOfSpeech() {
                binding.tvListeningHint.text = getString(R.string.recognizing)
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                binding.tvListeningHint.text = getString(R.string.processing)
            }

            override fun onError(error: Int) {
                viewModel.setListening(false)
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> getString(R.string.error_no_match)
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> getString(R.string.error_timeout)
                    else -> getString(R.string.error_voice_input)
                }
                showSnackbar(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                viewModel.setListening(false)
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    binding.etSourceText.setText(matches[0])
                    binding.etSourceText.setSelection(matches[0].length)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    binding.tvListeningHint.text = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun speakTranslation() {
        val currentState = viewModel.uiState.value
        
        if (currentState.isSpeaking) {
            textToSpeech?.stop()
            viewModel.setSpeaking(false)
            return
        }

        if (!isTtsReady) {
            showSnackbar(getString(R.string.tts_not_ready))
            return
        }

        if (currentState.translatedText.isBlank()) {
            showSnackbar(getString(R.string.nothing_to_speak))
            return
        }

        val locale = if (currentState.targetLanguage == Language.HINDI) {
            Locale("hi", "IN")
        } else {
            Locale.ENGLISH
        }

        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            showSnackbar(getString(R.string.language_not_supported))
            return
        }

        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                runOnUiThread { viewModel.setSpeaking(true) }
            }

            override fun onDone(utteranceId: String?) {
                runOnUiThread { viewModel.setSpeaking(false) }
            }

            override fun onError(utteranceId: String?) {
                runOnUiThread { 
                    viewModel.setSpeaking(false)
                    showSnackbar(getString(R.string.tts_error))
                }
            }
        })

        textToSpeech?.speak(
            currentState.translatedText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "translation"
        )
    }

    private fun copyToClipboard() {
        val translatedText = viewModel.uiState.value.translatedText
        if (translatedText.isBlank()) {
            showSnackbar(getString(R.string.nothing_to_copy))
            return
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Translation", translatedText)
        clipboard.setPrimaryClip(clip)
        showSnackbar(getString(R.string.copied_to_clipboard))
    }

    private fun shareTranslation() {
        val currentState = viewModel.uiState.value
        if (currentState.translatedText.isBlank()) {
            showSnackbar(getString(R.string.nothing_to_share))
            return
        }

        val shareText = """
            ${currentState.sourceLanguage.name}: ${currentState.sourceText}
            
            ${currentState.targetLanguage.name}: ${currentState.translatedText}
            
            - Translated with Voice Translator
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_translation)))
    }

    private fun animateSwap() {
        binding.btnSwapLanguages.animate()
            .rotationBy(180f)
            .setDuration(300)
            .start()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onInit(status: Int) {
        isTtsReady = status == TextToSpeech.SUCCESS
        if (!isTtsReady) {
            showSnackbar(getString(R.string.tts_init_failed))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
    }
}
