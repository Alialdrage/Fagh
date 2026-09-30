package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArabicTextNormalizer
import com.example.data.SearchPreferencesRepository
import com.example.data.repository.FatwaRepository
import com.example.model.Fatwa
import com.example.model.FatwaCategory
import com.example.model.RulingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FatwaUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedCategory: FatwaCategory? = null,
    val selectedSubCategory: String? = null,
    val selectedRulingType: RulingType? = null,
    val onlyBookmarked: Boolean = false,
    val onlyWithNotes: Boolean = false,
    val fontSizeScale: Float = 1.0f,
    val selectedFatwaForDetail: Fatwa? = null,
    val offlineBannerVisible: Boolean = true,
    val cacheStatusMessage: String? = null
)

class FatwaViewModel(application: Application) : AndroidViewModel(application) {

    private val searchRepo = SearchPreferencesRepository(application)
    private val fatwaRepo = FatwaRepository.getInstance(application)

    private val _uiState = MutableStateFlow(FatwaUiState())
    val uiState: StateFlow<FatwaUiState> = _uiState

    init {
        // Ensure local Room database has cached rulings ready immediately for offline use
        viewModelScope.launch {
            fatwaRepo.ensureDataPopulated()
        }
    }

    val totalCachedCount: StateFlow<Int> = fatwaRepo.cachedCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val recentQueries: StateFlow<List<String>> = searchRepo.recentQueries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Room database reactive stream of fatwas, combined with UI filters
    val filteredFatwas: StateFlow<List<Fatwa>> = combine(
        fatwaRepo.allFatwas,
        _uiState
    ) { allRulings, state ->
        val query = state.searchQuery.trim()
        val cat = state.selectedCategory
        val subCat = state.selectedSubCategory
        val ruling = state.selectedRulingType
        val onlySaved = state.onlyBookmarked
        val onlyNotes = state.onlyWithNotes

        allRulings.filter { fatwa ->
            // Category filter
            if (cat != null && fatwa.category != cat) {
                return@filter false
            }

            // SubCategory filter
            if (subCat != null && subCat != "الكل" && fatwa.subCategory != subCat) {
                return@filter false
            }

            // Ruling type filter
            if (ruling != null && fatwa.rulingType != ruling) {
                return@filter false
            }

            // Bookmarked filter
            if (onlySaved && !fatwa.isBookmarked) {
                return@filter false
            }

            // Notes filter
            if (onlyNotes && fatwa.userNotes.isBlank()) {
                return@filter false
            }

            // Search query filter (using Arabic normalization)
            if (query.isNotBlank()) {
                val matchesQuestion = ArabicTextNormalizer.matches(fatwa.question, query)
                val matchesTitle = ArabicTextNormalizer.matches(fatwa.title, query)
                val matchesAnswer = ArabicTextNormalizer.matches(fatwa.answer, query)
                val matchesSubCategory = ArabicTextNormalizer.matches(fatwa.subCategory, query)
                val matchesTags = fatwa.tags.any { ArabicTextNormalizer.matches(it, query) }
                val matchesSource = ArabicTextNormalizer.matches(fatwa.sourceBook, query)
                val matchesNotes = fatwa.userNotes.isNotBlank() && ArabicTextNormalizer.matches(fatwa.userNotes, query)

                if (!matchesQuestion && !matchesTitle && !matchesAnswer && !matchesSubCategory && !matchesTags && !matchesSource && !matchesNotes) {
                    return@filter false
                }
            }

            true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Bookmarked count computed reactively from Room stream
    val bookmarkedCount: StateFlow<Int> = fatwaRepo.allFatwas.combine(_uiState) { rulings, _ ->
        rulings.count { it.isBookmarked }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val notesCount: StateFlow<Int> = fatwaRepo.allFatwas.combine(_uiState) { rulings, _ ->
        rulings.count { it.userNotes.isNotBlank() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
    }

    fun setSearchActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = active)
    }

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        _uiState.value = _uiState.value.copy(searchQuery = trimmed, isSearchActive = false)
        if (trimmed.isNotBlank()) {
            viewModelScope.launch {
                searchRepo.saveSearchQuery(trimmed)
            }
        }
    }

    fun selectRecentQuery(query: String) {
        submitSearch(query)
    }

    fun removeRecentQuery(query: String) {
        viewModelScope.launch {
            searchRepo.removeSearchQuery(query)
        }
    }

    fun clearAllRecentQueries() {
        viewModelScope.launch {
            searchRepo.clearAllRecentQueries()
        }
    }

    fun selectCategory(category: FatwaCategory?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            selectedSubCategory = null
        )
    }

    fun selectSubCategory(subCategory: String?) {
        _uiState.value = _uiState.value.copy(
            selectedSubCategory = if (subCategory == "الكل") null else subCategory
        )
    }

    fun selectRulingType(rulingType: RulingType?) {
        _uiState.value = _uiState.value.copy(selectedRulingType = rulingType)
    }

    fun toggleOnlyBookmarked() {
        _uiState.value = _uiState.value.copy(
            onlyBookmarked = !_uiState.value.onlyBookmarked,
            onlyWithNotes = false
        )
    }

    fun toggleOnlyNotes() {
        _uiState.value = _uiState.value.copy(
            onlyWithNotes = !_uiState.value.onlyWithNotes,
            onlyBookmarked = false
        )
    }

    fun toggleBookmark(fatwaId: String) {
        viewModelScope.launch {
            fatwaRepo.toggleBookmark(fatwaId)
            // If detail dialog is currently showing this fatwa, update its state
            val currentDetail = _uiState.value.selectedFatwaForDetail
            if (currentDetail?.id == fatwaId) {
                _uiState.value = _uiState.value.copy(
                    selectedFatwaForDetail = currentDetail.copy(isBookmarked = !currentDetail.isBookmarked)
                )
            }
        }
    }

    fun saveUserNotes(fatwaId: String, notes: String) {
        viewModelScope.launch {
            fatwaRepo.updateUserNotes(fatwaId, notes)
            val currentDetail = _uiState.value.selectedFatwaForDetail
            if (currentDetail?.id == fatwaId) {
                _uiState.value = _uiState.value.copy(
                    selectedFatwaForDetail = currentDetail.copy(userNotes = notes)
                )
            }
            _uiState.value = _uiState.value.copy(cacheStatusMessage = "تم حفظ الملاحظة محلياً بنجاح")
        }
    }

    fun refreshOfflineDatabase() {
        viewModelScope.launch {
            fatwaRepo.refreshOfflineDatabase()
            _uiState.value = _uiState.value.copy(
                cacheStatusMessage = "تم تحديث ومزامنة قاعدة البيانات المحلية (Room) بنجاح"
            )
        }
    }

    fun dismissCacheStatusMessage() {
        _uiState.value = _uiState.value.copy(cacheStatusMessage = null)
    }

    fun setDetailFatwa(fatwa: Fatwa?) {
        _uiState.value = _uiState.value.copy(selectedFatwaForDetail = fatwa)
    }

    fun setOfflineBannerVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(offlineBannerVisible = visible)
    }

    fun increaseFontSize() {
        if (_uiState.value.fontSizeScale < 1.4f) {
            _uiState.value = _uiState.value.copy(fontSizeScale = _uiState.value.fontSizeScale + 0.15f)
        }
    }

    fun decreaseFontSize() {
        if (_uiState.value.fontSizeScale > 0.85f) {
            _uiState.value = _uiState.value.copy(fontSizeScale = _uiState.value.fontSizeScale - 0.15f)
        }
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedCategory = null,
            selectedSubCategory = null,
            selectedRulingType = null,
            onlyBookmarked = false,
            onlyWithNotes = false
        )
    }

    fun getAvailableSubCategories(): List<String> {
        val cat = _uiState.value.selectedCategory ?: return emptyList()
        return filteredFatwas.value
            .filter { it.category == cat }
            .map { it.subCategory }
            .distinct()
    }
}
