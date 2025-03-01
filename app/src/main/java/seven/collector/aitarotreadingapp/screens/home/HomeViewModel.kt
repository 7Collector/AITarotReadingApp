package seven.collector.aitarotreadingapp.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao
import seven.collector.aitarotreadingapp.helpers.SpeechToTextHelper

class HomeViewModel(private val readingDao: ReadingDao, private var speechHelper: SpeechToTextHelper) : ViewModel() {
    private val _previousReadings = MutableStateFlow<List<Reading>>(emptyList())
    val previousReadings: StateFlow<List<Reading>> = _previousReadings


    private val _listeningMode = MutableStateFlow(false)
    val listeningMode: StateFlow<Boolean> = _listeningMode

    private val _inputMode = MutableStateFlow(false)
    val inputMode: StateFlow<Boolean> = _inputMode

    var speechText = mutableStateOf("")

    init {
        speechHelper.setViewModel(this)
        speechHelper.setOnResult {
            updateSpeechText(it)
        }
        speechHelper.start()
        loadPreviousReadings()
    }

    private fun loadPreviousReadings() {
        viewModelScope.launch {
            readingDao.getLastThreeReadings().collect { readings ->
                _previousReadings.value = readings
            }
        }
    }

    fun addNewReading(reading: Reading, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = readingDao.insertReading(reading)
            onResult(newId)
        }
    }

    fun updateSpeechText(text: String) {
        Log.d("HomeViewModel", "Updating speech text: $text")
        speechText.value = text
    }

    fun startListening() {
        if(!inputMode.value) {
            _inputMode.value = true
        }
        if (!listeningMode.value) {
            _listeningMode.value = true
            Log.d("HomeViewModel", "Starting Speech Recognition")
            speechHelper.startListening()
        }
    }

    fun stopSTT() {
        speechHelper.stopListening()
    }

    fun stopListening() {
        _listeningMode.value = false
        Log.d("HomeViewModel", "Stopping Speech Recognition")
    }

    fun startListeningAgain() {
        _listeningMode.value = false
        speechHelper.destroy()
        speechHelper.start()
        startListening()
    }

    fun setInputMode(v: Boolean) {
        _inputMode.value = v
    }
}