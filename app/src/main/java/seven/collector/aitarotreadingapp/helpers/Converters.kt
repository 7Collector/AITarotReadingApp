package seven.collector.aitarotreadingapp.helpers

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import seven.collector.aitarotreadingapp.database.models.Card
import seven.collector.aitarotreadingapp.database.models.Chat

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromCardList(cards: List<Card>): String {
        return gson.toJson(cards)
    }

    @TypeConverter
    fun toCardList(data: String): List<Card> {
        val listType = object : TypeToken<List<Card>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromChatList(chats: List<Chat>): String {
        return gson.toJson(chats)
    }

    @TypeConverter
    fun toChatList(data: String): List<Chat> {
        val listType = object : TypeToken<List<Chat>>() {}.type
        return gson.fromJson(data, listType)
    }
}
