package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FatwaDao {
    @Query("SELECT * FROM fatwas ORDER BY questionNumber ASC")
    fun getAllFatwas(): Flow<List<FatwaEntity>>

    @Query("SELECT * FROM fatwas WHERE category = :category ORDER BY questionNumber ASC")
    fun getFatwasByCategory(category: String): Flow<List<FatwaEntity>>

    @Query("SELECT * FROM fatwas WHERE isBookmarked = 1 ORDER BY questionNumber ASC")
    fun getBookmarkedFatwas(): Flow<List<FatwaEntity>>

    @Query("SELECT * FROM fatwas WHERE userNotes != '' ORDER BY questionNumber ASC")
    fun getFatwasWithNotes(): Flow<List<FatwaEntity>>

    @Query("SELECT * FROM fatwas WHERE id = :id LIMIT 1")
    fun getFatwaById(id: String): Flow<FatwaEntity?>

    @Query("SELECT * FROM fatwas WHERE id = :id LIMIT 1")
    suspend fun getFatwaByIdOnce(id: String): FatwaEntity?

    @Query("SELECT COUNT(*) FROM fatwas")
    fun getFatwaCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM fatwas")
    suspend fun getFatwaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatwas(fatwas: List<FatwaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatwa(fatwa: FatwaEntity)

    @Update
    suspend fun updateFatwa(fatwa: FatwaEntity)

    @Query("UPDATE fatwas SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE fatwas SET userNotes = :notes WHERE id = :id")
    suspend fun updateUserNotes(id: String, notes: String)

    @Query("DELETE FROM fatwas WHERE id = :id")
    suspend fun deleteFatwaById(id: String)

    @Query("DELETE FROM fatwas")
    suspend fun clearAll()
}
