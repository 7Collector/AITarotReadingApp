package seven.collector.aitarotreadingapp.screens.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class ReadingViewModelFactory(
    private val id: Int,
    private val readingDao: ReadingDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingViewModel::class.java)) {
            return ReadingViewModel(id, readingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
