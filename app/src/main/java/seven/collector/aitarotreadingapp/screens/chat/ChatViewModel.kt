package seven.collector.aitarotreadingapp.screens.chat

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Chat
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao
import seven.collector.aitarotreadingapp.helpers.aiChat
import java.util.Locale

class ChatViewModel(
    private val readingDao: ReadingDao,
    private val readingId: Int,
    private val context: Context
) : ViewModel() {

    private val _reading = MutableStateFlow<Reading?>(null)
    val reading: StateFlow<Reading?> = _reading

    private val _isAiResponding = MutableStateFlow(false)
    val isAiResponding: StateFlow<Boolean> = _isAiResponding

    private val _isVoiceMessage = MutableStateFlow(false)
    val isVoiceMessage: StateFlow<Boolean> = _isVoiceMessage
    private var textToSpeech: TextToSpeech? = null
    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText

    private var isTtsInitialized = false

    private fun initTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                isTtsInitialized = true // Mark TTS as ready
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("TTS", "Speaking started")
                    }

                    override fun onDone(utteranceId: String?) {
                        startListening()
                        Log.d("TTS", "Speaking finished")
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("TTS", "Speaking error")
                    }
                })
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }


    init {
        initTTS()
        fetchReading()
    }

    fun startListening() {
        _isVoiceMessage.value = true
        val speechRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(context)
        val speechIntent =
            android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_LANGUAGE,
                    java.util.Locale.getDefault()
                )
                putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches =
                    results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let {
                    _chatInputText.value = it
                    _isVoiceMessage.value = true
                }
            }

            override fun onError(error: Int) {}
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
        speechRecognizer.startListening(speechIntent)
    }

    private fun fetchReading() {
        viewModelScope.launch {
            _reading.value = readingDao.getReadingById(readingId)
        }
    }

    fun onChatInputChange(newText: String) {
        _chatInputText.value = newText
    }

    fun sendMessage() {
        val currentReading = _reading.value
        if (chatInputText.value.isNotBlank() && currentReading != null) {
            val userMessage = Chat(sender = "user", text = chatInputText.value)
            val updatedChats = currentReading.chats + userMessage
            _reading.value = currentReading.copy(chats = updatedChats)
            saveReading()
            _chatInputText.value = ""
            aiRespond(updatedChats)
        }
    }

    private fun aiRespond(updatedChats: List<Chat>) {
        _isAiResponding.value = true
        val loadingMessage = Chat(sender = "ai", text = "...")
        _reading.value = _reading.value?.copy(chats = updatedChats + loadingMessage)

        viewModelScope.launch {
            val sendData = mapOf(
                "cards" to reading.value?.cards,
                "message" to reading.value?.chats?.last()?.text,
                "question" to reading.value?.question,
                "history" to reading.value?.chats,
            )
            val aiResponseText = aiChat.sendMessage(sendData.toString()).text
            if (_isVoiceMessage.value && isTtsInitialized) {
                Log.d("AIRespond", "Speaking: $aiResponseText")
                textToSpeech?.setPitch(0.8f)
                textToSpeech?.setSpeechRate(0.9f)
                textToSpeech?.speak(aiResponseText, TextToSpeech.QUEUE_FLUSH, null, "AI_RESPONSE")
            } else {
                Log.e("AIRespond", "TTS not ready")
            }

            val aiResponse =
                Chat(sender = "ai", text = aiResponseText ?: "I see something interesting...")
            _reading.value = _reading.value?.copy(chats = updatedChats + aiResponse)
            saveReading()
            _isAiResponding.value = false
        }
    }

    private fun saveReading() {
        viewModelScope.launch {
            reading.value?.let { readingDao.updateReading(it) }
        }
    }
}
