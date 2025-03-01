package seven.collector.aitarotreadingapp.database.reading

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import seven.collector.aitarotreadingapp.database.models.Reading

@Dao
interface ReadingDao {

    @Query("SELECT * FROM readings ORDER BY id DESC")
    fun getAllReadings(): Flow<List<Reading>> {
        Log.d("ReadingDao", "Fetching all readings")
        val result = getAllReadingsInternal()
        Log.d("ReadingDao", "Result: $result")
        return result
    }

    @Query("SELECT * FROM readings ORDER BY id DESC LIMIT 3")
    fun getLastThreeReadings(): Flow<List<Reading>> {
        Log.d("ReadingDao", "Fetching last three readings")
        val result = getLastThreeReadingsInternal()
        Log.d("ReadingDao", "Result: $result")
        return result
    }

    @Query("SELECT * FROM readings WHERE id = :readingId")
    suspend fun getReadingById(readingId: Int): Reading? {
        Log.d("ReadingDao", "Fetching reading with id: $readingId")
        val result = getReadingByIdInternal(readingId)
        Log.d("ReadingDao", "Result: $result")
        return result
    }

    @Update
    suspend fun updateReading(reading: Reading) {
        Log.d("ReadingDao", "Updating reading with id: ${reading.id}")
        updateReadingInternal(reading)
        Log.d("ReadingDao", "Update completed for id: ${reading.id}")
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: Reading): Long {
        Log.d("ReadingDao", "Inserting reading with id: ${reading.id}")
        val result = insertReadingInternal(reading)
        Log.d("ReadingDao", "Insert result: $result")
        return result
    }

    @Query("DELETE FROM readings WHERE id = :readingId")
    suspend fun deleteReading(readingId: Int) {
        Log.d("ReadingDao", "Deleting reading with id: $readingId")
        deleteReadingInternal(readingId)
        Log.d("ReadingDao", "Deletion completed for id: $readingId")
    }

    // Internal functions to prevent recursion
    @Query("SELECT * FROM readings ORDER BY id DESC")
    fun getAllReadingsInternal(): Flow<List<Reading>>

    @Query("SELECT * FROM readings ORDER BY id DESC LIMIT 3")
    fun getLastThreeReadingsInternal(): Flow<List<Reading>>

    @Query("SELECT * FROM readings WHERE id = :readingId")
    suspend fun getReadingByIdInternal(readingId: Int): Reading?

    @Update
    suspend fun updateReadingInternal(reading: Reading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingInternal(reading: Reading): Long

    @Query("DELETE FROM readings WHERE id = :readingId")
    suspend fun deleteReadingInternal(readingId: Int)
}
