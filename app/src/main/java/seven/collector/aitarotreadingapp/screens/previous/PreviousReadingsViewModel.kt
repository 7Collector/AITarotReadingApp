package seven.collector.aitarotreadingapp.screens.previous

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class PreviousReadingsViewModel(private val readingDao: ReadingDao) : ViewModel() {

    private val _readings = MutableStateFlow<List<Reading>>(emptyList())
    val readings: StateFlow<List<Reading>> = _readings

    init {
        fetchReadings()
    }

    private fun fetchReadings() {
        viewModelScope.launch {
            readingDao.getAllReadings().collect { value ->
                _readings.value = value
            }
        }
    }
}

class PreviousReadingsViewModelFactory(private val readingDao: ReadingDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreviousReadingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreviousReadingsViewModel(readingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
