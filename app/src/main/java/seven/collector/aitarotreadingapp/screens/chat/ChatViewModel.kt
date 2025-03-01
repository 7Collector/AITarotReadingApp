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
import seven.collector.aitarotreadingapp.helpers.aiChat

class ChatViewModel(private val readingDao: ReadingDao, private val readingId: Int) : ViewModel() {

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
            val aiResponse = Chat(sender = "ai", text = aiResponseText ?: "I see something interesting...")
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
