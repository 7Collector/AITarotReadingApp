package seven.collector.aitarotreadingapp

import android.app.Application
import androidx.room.Room
import seven.collector.aitarotreadingapp.database.reading.ReadingDB

class TarotApp : Application() {
    val database: ReadingDB by lazy {
        Room.databaseBuilder(
            this,
            ReadingDB::class.java,
            "reading_database"
        )
            .build()
    }
}
