package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sistani_fatwa_prefs")

class SearchPreferencesRepository(private val context: Context) {

    companion object {
        private val RECENT_QUERIES_KEY = stringPreferencesKey("recent_search_queries_list")
        private val BOOKMARKS_KEY = stringSetPreferencesKey("bookmarked_fatwa_ids")
        private const val DELIMITER = "###_SEP_###"
        private const val MAX_RECENT_QUERIES = 12
    }

    /**
     * Flow of recent search queries in reverse chronological order (newest first).
     */
    val recentQueries: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val raw = preferences[RECENT_QUERIES_KEY] ?: ""
        if (raw.isBlank()) {
            emptyList()
        } else {
            raw.split(DELIMITER).filter { it.isNotBlank() }
        }
    }

    /**
     * Flow of bookmarked fatwa IDs.
     */
    val bookmarkedFatwaIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[BOOKMARKS_KEY] ?: emptySet()
    }

    /**
     * Adds a search query to the top of recent queries list without duplicates.
     */
    suspend fun saveSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        context.dataStore.edit { preferences ->
            val currentRaw = preferences[RECENT_QUERIES_KEY] ?: ""
            val currentList = if (currentRaw.isBlank()) {
                mutableListOf()
            } else {
                currentRaw.split(DELIMITER).filter { it.isNotBlank() }.toMutableList()
            }

            // Remove existing match case-insensitively
            currentList.removeAll { it.equals(trimmed, ignoreCase = true) }
            // Insert at front
            currentList.add(0, trimmed)

            // Keep within limit
            val trimmedList = if (currentList.size > MAX_RECENT_QUERIES) {
                currentList.take(MAX_RECENT_QUERIES)
            } else {
                currentList
            }

            preferences[RECENT_QUERIES_KEY] = trimmedList.joinToString(DELIMITER)
        }
    }

    /**
     * Removes a specific query from recent history.
     */
    suspend fun removeSearchQuery(queryToRemove: String) {
        context.dataStore.edit { preferences ->
            val currentRaw = preferences[RECENT_QUERIES_KEY] ?: return@edit
            val list = currentRaw.split(DELIMITER).filter { 
                it.isNotBlank() && !it.equals(queryToRemove.trim(), ignoreCase = true) 
            }
            preferences[RECENT_QUERIES_KEY] = list.joinToString(DELIMITER)
        }
    }

    /**
     * Clears all recent searches.
     */
    suspend fun clearAllRecentQueries() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_QUERIES_KEY)
        }
    }

    /**
     * Toggles a fatwa's bookmark status.
     */
    suspend fun toggleBookmark(fatwaId: String) {
        context.dataStore.edit { preferences ->
            val currentBookmarks = preferences[BOOKMARKS_KEY]?.toMutableSet() ?: mutableSetOf()
            if (currentBookmarks.contains(fatwaId)) {
                currentBookmarks.remove(fatwaId)
            } else {
                currentBookmarks.add(fatwaId)
            }
            preferences[BOOKMARKS_KEY] = currentBookmarks
        }
    }
}
