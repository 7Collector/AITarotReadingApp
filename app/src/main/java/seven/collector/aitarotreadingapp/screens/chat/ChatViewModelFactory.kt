package seven.collector.aitarotreadingapp.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

class ChatViewModelFactory(
    private val readingDao: ReadingDao,
    private val readingId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(readingDao, readingId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}