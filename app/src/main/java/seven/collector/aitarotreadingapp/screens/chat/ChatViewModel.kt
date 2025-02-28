package seven.collector.aitarotreadingapp.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.reading.ReadingDao
import seven.collector.aitarotreadingapp.database.models.Chat
import seven.collector.aitarotreadingapp.database.models.Reading

class ChatViewModel(private val readingDao: ReadingDao, private val readingId: String) : ViewModel() {

    private val _reading = MutableStateFlow<Reading?>(null)
    val reading: StateFlow<Reading?> = _reading

    private val _isAiResponding = MutableStateFlow(false)
    val isAiResponding: StateFlow<Boolean> = _isAiResponding

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText

    init {
        fetchReading()
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
            val newChat = Chat(sender = "user", text = chatInputText.value)
            val updatedChats = currentReading.chats + newChat
            _reading.value = currentReading.copy(chats = updatedChats)
            _chatInputText.value = ""
            aiRespond()
        }
    }

    private fun aiRespond() {
        _isAiResponding.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val currentReading = _reading.value
            if (currentReading != null) {
                val aiResponse = Chat(sender = "ai", text = "This is an AI-generated response.")
                val updatedChats = currentReading.chats + aiResponse
                _reading.value = currentReading.copy(chats = updatedChats)
            }
            _isAiResponding.value = false
        }
    }
}