package seven.collector.aitarotreadingapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import seven.collector.aitarotreadingapp.database.reading.ReadingDao
import seven.collector.aitarotreadingapp.helpers.SpeechToTextHelper

class HomeViewModelFactory(private val readingDao: ReadingDao, private val sttHelper: SpeechToTextHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(readingDao, sttHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}