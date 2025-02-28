package seven.collector.aitarotreadingapp.database.reading

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import seven.collector.aitarotreadingapp.database.models.Reading
import seven.collector.aitarotreadingapp.helpers.Converters

@Database(entities = [Reading::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReadingDB : RoomDatabase() {
    abstract fun readingDao(): ReadingDao
}