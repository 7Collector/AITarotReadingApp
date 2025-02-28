package seven.collector.aitarotreadingapp.database.reading

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import seven.collector.aitarotreadingapp.database.models.Reading

@Dao
interface ReadingDao {

    @Query("SELECT * FROM readings ORDER BY date DESC")
    fun getAllReadings(): Flow<List<Reading>>

    @Query("SELECT * FROM readings ORDER BY date DESC LIMIT 3")
    fun getLastThreeReadings(): Flow<List<Reading>>

    @Query("SELECT * FROM readings WHERE id = :readingId")
    suspend fun getReadingById(readingId: kotlin.String): Reading?

    @Update
    suspend fun updateReading(reading: Reading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: Reading) : Long

    @Query("DELETE FROM readings WHERE id = :readingId")
    suspend fun deleteReading(readingId: Int)
}
