package seven.collector.aitarotreadingapp.database.models

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "readings")
data class Reading(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val question: String,
    val aiInterpretation: String,
    val date: String,
    val title: String,
    val cards: List<Card>,
    val chats: List<Chat>
)
