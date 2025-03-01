package seven.collector.aitarotreadingapp.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import seven.collector.aitarotreadingapp.database.models.Card
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.database.reading.ReadingDao

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
        }
    }

    fun getSelectedCards(): MutableList<Card> {
        return selectedCards
    }

    fun saveReadingWithCards() {
        if (selectedCards.size == 3) {
            viewModelScope.launch {
                val reading = readingDao.getReadingById(id)?.copy(
                    cards = selectedCards
                )
                if (reading != null) {
                    readingDao.insertReading(reading)
                }
            }
        }
    }

    fun saveReadingWithAI(title: String, interpretation: String) {
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
