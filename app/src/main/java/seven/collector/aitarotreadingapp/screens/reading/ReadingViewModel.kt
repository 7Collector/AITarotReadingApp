package seven.collector.aitarotreadingapp.screens.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class ReadingViewModel(private val id: Int, private val readingDao: ReadingDao) : ViewModel() {

    private val _reading = MutableStateFlow<Reading?>(null)
    val reading: StateFlow<Reading?> get() = _reading

    init {
        viewModelScope.launch {
            _reading.value = loadReading(id)
        }
    }

    private suspend fun loadReading(id: Int): Reading? {
        return readingDao.getReadingById(id)
    }

    fun deleteReading(onResult: () -> Unit) {
        viewModelScope.launch {
            readingDao.deleteReading(id)
            onResult()
        }
    }
}
