package seven.collector.aitarotreadingapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class HomeViewModelFactory(private val readingDao: ReadingDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(readingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}