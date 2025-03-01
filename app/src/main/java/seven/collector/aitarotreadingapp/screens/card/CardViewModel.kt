package seven.collector.aitarotreadingapp.screens.card

import android.util.Log
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Card
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao
import seven.collector.aitarotreadingapp.helpers.tarotReadingModel
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CardViewModel(private val id: Int, private val readingDao: ReadingDao) : ViewModel() {
    private val selectedCards = mutableListOf<Card>()

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

    fun selectCard(card: Card) {
        if (selectedCards.size < 3) {
            selectedCards.add(card)
            if (selectedCards.size == 3) {
                saveReadingWithCards()
            }
            Log.d("AIInterpretation", "SelectedCards: $selectedCards")
        }
    }

    private fun saveReadingWithCards() {
        viewModelScope.launch {
            val reading = readingDao.getReadingById(id)?.copy(
                cards = selectedCards
            )
            if (reading != null) {
                readingDao.insertReading(reading)
            }
        }
    }

    private fun saveReadingWithAI(title: String, interpretation: String) {
        if (selectedCards.size == 3) {
            viewModelScope.launch {
                val reading = readingDao.getReadingById(id)?.copy(
                    title = title,
                    aiInterpretation = interpretation
                )
                if (reading != null) {
                    readingDao.insertReading(reading)
                }
            }
        }
    }

    fun sendMessage(onResponse: (String, String) -> Unit) {
        aiRespond { title, interpretation ->
            Log.d("AIInterpretation", "Title:${title} Interpretation:${interpretation}")
            onResponse(title, interpretation)
        }
    }

    private fun aiRespond(onResult: (String, String) -> Unit) {
        viewModelScope.launch {
            Log.d("AIInterpretation", "aiRespond function called")
            try {
                // Use only card names for simplicity
                val sendData = mapOf(
                    "cards" to selectedCards.map { it.name },
                    "question" to reading.value?.question
                )
                Log.d("AIInterpretation", "SendData: $sendData")
                // Serialize sendData to JSON
                val gson = Gson()
                val sendDataJson = gson.toJson(sendData)

                val aiResponseText = tarotReadingModel.generateContent(sendDataJson)
                Log.d("AIInterpretation", "Raw AI Response: ${aiResponseText.text}")

                val responseMap: Map<String, String> = try {
                    gson.fromJson(
                        aiResponseText.text,
                        object : TypeToken<Map<String, String>>() {}.type
                    )
                } catch (e: JsonSyntaxException) {
                    Log.e("AIInterpretation", "Invalid JSON response: ${e.message}", e)
                    onResult("Error", "Failed to parse AI response")
                    return@launch
                }

                val title = responseMap["title"] ?: "Unknown Title"
                val interpretation = responseMap["interpretation"] ?: "No interpretation available"

                saveReadingWithAI(title, interpretation)
                onResult(title, interpretation)

            } catch (e: CancellationException) {
                Log.i("AIInterpretation", "Coroutine was cancelled: ${e.message}")
            } catch (e: IOException) {
                Log.e("AIInterpretation", "Network error: ${e.message}", e)
                onResult("Error", "Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("AIInterpretation", "Exception in aiRespond: ${e.message}", e)
                onResult("Error", "Failed to generate interpretation due to error: ${e.message}")
            }
        }
    }

}

class CardViewModelFactory(
    private val id: Int, private val readingDao: ReadingDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return CardViewModel(id, readingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}