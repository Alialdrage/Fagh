package com.example.data.repository

import android.content.Context
import com.example.data.FatwaDatabase
import com.example.data.local.FatwaDao
import com.example.data.local.FatwaEntity
import com.example.data.local.FatwaRoomDatabase
import com.example.model.Fatwa
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository abstracting Room Database data access for fatwas and offline caching.
 */
class FatwaRepository(
    private val fatwaDao: FatwaDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Reactive stream of all cached fatwas from local Room SQLite database.
     */
    val allFatwas: Flow<List<Fatwa>> = fatwaDao.getAllFatwas().map { list ->
        list.map { it.toFatwa() }
    }

    /**
     * Reactive stream of bookmarked fatwas from Room.
     */
    val bookmarkedFatwas: Flow<List<Fatwa>> = fatwaDao.getBookmarkedFatwas().map { list ->
        list.map { it.toFatwa() }
    }

    /**
     * Reactive stream of fatwas with personal user notes.
     */
    val fatwasWithNotes: Flow<List<Fatwa>> = fatwaDao.getFatwasWithNotes().map { list ->
        list.map { it.toFatwa() }
    }

    /**
     * Count of total fatwas cached locally in Room database.
     */
    val cachedCount: Flow<Int> = fatwaDao.getFatwaCountFlow()

    /**
     * Prepopulates Room database with essential fatwas if empty, ensuring instant offline availability.
     */
    suspend fun ensureDataPopulated() = withContext(ioDispatcher) {
        val currentCount = fatwaDao.getFatwaCount()
        if (currentCount == 0) {
            val entities = FatwaDatabase.sampleFatwas.map { fatwa ->
                FatwaEntity.fromFatwa(fatwa.copy(isOfflineCached = true, cachedTimestamp = System.currentTimeMillis()))
            }
            fatwaDao.insertFatwas(entities)
        }
    }

    /**
     * Toggles bookmark state for a fatwa in Room.
     */
    suspend fun toggleBookmark(fatwaId: String) = withContext(ioDispatcher) {
        val entity = fatwaDao.getFatwaByIdOnce(fatwaId) ?: return@withContext
        fatwaDao.updateBookmark(fatwaId, !entity.isBookmarked)
    }

    /**
     * Updates offline user note for a specific fatwa in Room.
     */
    suspend fun updateUserNotes(fatwaId: String, notes: String) = withContext(ioDispatcher) {
        fatwaDao.updateUserNotes(fatwaId, notes.trim())
    }

    /**
     * Force re-caches and refreshes the baseline content into Room database.
     */
    suspend fun refreshOfflineDatabase() = withContext(ioDispatcher) {
        val entities = FatwaDatabase.sampleFatwas.map { fatwa ->
            // Preserve user notes & bookmarks if already present in database
            val existing = fatwaDao.getFatwaByIdOnce(fatwa.id)
            FatwaEntity.fromFatwa(
                fatwa.copy(
                    isBookmarked = existing?.isBookmarked ?: false,
                    userNotes = existing?.userNotes ?: "",
                    isOfflineCached = true,
                    cachedTimestamp = System.currentTimeMillis()
                )
            )
        }
        fatwaDao.insertFatwas(entities)
    }

    companion object {
        @Volatile
        private var INSTANCE: FatwaRepository? = null

        fun getInstance(context: Context): FatwaRepository {
            return INSTANCE ?: synchronized(this) {
                val db = FatwaRoomDatabase.getDatabase(context)
                val instance = FatwaRepository(db.fatwaDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
