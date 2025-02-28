package seven.collector.aitarotreadingapp.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class HomeViewModel(private val readingDao: ReadingDao) : ViewModel() {
    private val _previousReadings = MutableStateFlow<List<Reading>>(emptyList())
    val previousReadings: StateFlow<List<Reading>> = _previousReadings
    var inputMode = mutableStateOf(false)

    init {
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
}