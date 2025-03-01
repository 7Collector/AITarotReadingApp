package seven.collector.aitarotreadingapp.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import seven.collector.aitarotreadingapp.screens.home.HomeViewModel

class SpeechToTextHelper(
    context: Context,
) {
    private lateinit var onResult: (String) -> Unit
    private lateinit var viewModel: HomeViewModel
    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    fun setViewModel(hVM: HomeViewModel) {
        viewModel = hVM
    }

    fun setOnResult(listener: (String) -> Unit) {
        onResult = listener
    }

    fun start() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("SpeechToTextHelper", "Speech ended.")
                if (viewModel.speechText.value.isEmpty()) {
                    //viewModel.inputMode.value = false // Reset input mode if no text
                }
                stopListening()
            }

            override fun onError(error: Int) {

                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                    // No speech detected for too long, reset input mode
                    // viewModel.inputMode.value = false
                }

                stopListening() // Ensure STT stops on error
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechToTextHelper", "Received results")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let {
                    Log.d("SpeechToTextHelper", "Recognized text: $it")
                    onResult(it)
                } ?: Log.d("SpeechToTextHelper", "No recognized text")
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val speechIntent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    java.util.Locale.getDefault()
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
        speechRecognizer.startListening(speechIntent)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        viewModel.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}
