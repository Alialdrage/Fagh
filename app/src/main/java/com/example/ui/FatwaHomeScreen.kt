package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CategoryFilterBar
import com.example.ui.components.FatwaCard
import com.example.ui.components.FatwaDetailDialog
import com.example.ui.components.FatwaSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatwaHomeScreen(
    viewModel: FatwaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredFatwas by viewModel.filteredFatwas.collectAsStateWithLifecycle()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val totalCachedCount by viewModel.totalCachedCount.collectAsStateWithLifecycle()
    val bookmarkedCount by viewModel.bookmarkedCount.collectAsStateWithLifecycle()
    val notesCount by viewModel.notesCount.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.cacheStatusMessage) {
        uiState.cacheStatusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissCacheStatusMessage()
        }
    }

    // Force RTL for correct Arabic typography & layout orientation
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("fatwa_home_screen"),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Mosque,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "فتاوى السيد السيستاني",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.OfflinePin,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "مخزنة محلياً • قراءة دون اتصال (Room)",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Toggle Offline Cache info banner
                                Surface(
                                    shape = CircleShape,
                                    color = if (uiState.offlineBannerVisible) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.setOfflineBannerVisible(!uiState.offlineBannerVisible) },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OfflinePin,
                                            contentDescription = "معلومات التخزين دون اتصال",
                                            tint = if (uiState.offlineBannerVisible) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Bookmarks Quick Toggle Badge
                                Surface(
                                    shape = CircleShape,
                                    color = if (uiState.onlyBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("top_bar_bookmark_toggle")
                                ) {
                                    IconButton(
                                        onClick = { viewModel.toggleOnlyBookmarked() },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "المحفوظات",
                                            tint = if (uiState.onlyBookmarked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Offline Room Database Status Banner
                    item {
                        AnimatedVisibility(
                            visible = uiState.offlineBannerVisible,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            OfflineCacheBanner(
                                cachedCount = totalCachedCount,
                                onRefreshCache = { viewModel.refreshOfflineDatabase() },
                                onDismiss = { viewModel.setOfflineBannerVisible(false) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Search Bar Section
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            FatwaSearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onSearch = { viewModel.submitSearch(it) },
                                isActive = uiState.isSearchActive,
                                onActiveChange = { viewModel.setSearchActive(it) },
                                recentQueries = recentQueries,
                                onSelectRecentQuery = { viewModel.selectRecentQuery(it) },
                                onRemoveRecentQuery = { viewModel.removeRecentQuery(it) },
                                onClearAllRecentQueries = { viewModel.clearAllRecentQueries() }
                            )
                        }
                    }

                    // Category & Subcategory Filter Section
                    item {
                        CategoryFilterBar(
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            availableSubCategories = viewModel.getAvailableSubCategories(),
                            selectedSubCategory = uiState.selectedSubCategory,
                            onSubCategorySelected = { viewModel.selectSubCategory(it) },
                            selectedRulingType = uiState.selectedRulingType,
                            onRulingTypeSelected = { viewModel.selectRulingType(it) },
                            onlyBookmarked = uiState.onlyBookmarked,
                            onToggleOnlyBookmarked = { viewModel.toggleOnlyBookmarked() },
                            bookmarkedCount = bookmarkedCount,
                            onlyWithNotes = uiState.onlyWithNotes,
                            onToggleOnlyNotes = { viewModel.toggleOnlyNotes() },
                            notesCount = notesCount,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    // Active Filters & Results Count Summary Bar
                    item {
                        val hasActiveFilters = uiState.searchQuery.isNotBlank() ||
                                uiState.selectedCategory != null ||
                                uiState.selectedSubCategory != null ||
                                uiState.selectedRulingType != null ||
                                uiState.onlyBookmarked ||
                                uiState.onlyWithNotes

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${filteredFatwas.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        uiState.onlyBookmarked -> "فتاوى محفوظة في المفضلة"
                                        uiState.onlyWithNotes -> "فتاوى تحتوي على ملاحظات شخصية"
                                        else -> "مسألة شرعية معتمدة ومخزنة أوفلاين"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (hasActiveFilters) {
                                TextButton(
                                    onClick = { viewModel.resetFilters() },
                                    modifier = Modifier.testTag("reset_all_filters_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterAltOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "إعادة ضبط التصفية",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Empty State or List of Fatwas
                    if (filteredFatwas.isEmpty()) {
                        item {
                            EmptySearchResultState(
                                onReset = { viewModel.resetFilters() },
                                searchQuery = uiState.searchQuery,
                                isBookmarkedOnly = uiState.onlyBookmarked,
                                isNotesOnly = uiState.onlyWithNotes
                            )
                        }
                    } else {
                        items(
                            items = filteredFatwas,
                            key = { it.id }
                        ) { fatwa ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                FatwaCard(
                                    fatwa = fatwa,
                                    isBookmarked = fatwa.isBookmarked,
                                    onToggleBookmark = { viewModel.toggleBookmark(fatwa.id) },
                                    onCardClick = { viewModel.setDetailFatwa(fatwa) },
                                    fontSizeScale = uiState.fontSizeScale
                                )
                            }
                        }
                    }
                }

                // Detail Dialog View
                uiState.selectedFatwaForDetail?.let { detailFatwa ->
                    FatwaDetailDialog(
                        fatwa = detailFatwa,
                        isBookmarked = detailFatwa.isBookmarked,
                        onToggleBookmark = { viewModel.toggleBookmark(detailFatwa.id) },
                        onSaveNotes = { notes -> viewModel.saveUserNotes(detailFatwa.id, notes) },
                        onDismiss = { viewModel.setDetailFatwa(null) },
                        fontSizeScale = uiState.fontSizeScale,
                        onIncreaseFontSize = { viewModel.increaseFontSize() },
                        onDecreaseFontSize = { viewModel.decreaseFontSize() }
                    )
                }
            }
        }
    }
}

/**
 * Offline Mode & Room Database Information Card
 */
@Composable
fun OfflineCacheBanner(
    cachedCount: Int,
    onRefreshCache: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_cache_banner"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.OfflinePin,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "وضع القراءة دون اتصال (Room SQLite)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$cachedCount مسألة فقهية مخزنة محلياً في ذاكرة جهازك",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إخفاء التنبيه",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "كافة الفتاوى والأحكام مخزنة في قاعدة بيانات Room محلية. يمكنك تصفح الأحكام، البحث فيها، وحفظ ملاحظاتك دون الحاجة إلى اتصال بالإنترنت.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "جاهز دون إنترنت 100%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onRefreshCache,
                    modifier = Modifier.testTag("refresh_cache_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة مزامنة الذاكرة", fontSize = 11.5.sp)
                }
            }
        }
    }
}

@Composable
fun EmptySearchResultState(
    onReset: () -> Unit,
    searchQuery: String,
    isBookmarkedOnly: Boolean,
    isNotesOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            isBookmarkedOnly -> Icons.Default.Bookmark
                            isNotesOnly -> Icons.Default.EditNote
                            else -> Icons.Default.SearchOff
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    isBookmarkedOnly -> "لا توجد فتاوى محفوظة بعد"
                    isNotesOnly -> "لا توجد فتاوى بملاحظات شخصية"
                    else -> "لم يتم العثور على فتاوى مطابقة"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    isBookmarkedOnly ->
                        "يمكنك الضغط على علامة الحفظ في أي فتوى للرجوع إليها لاحقاً بسهولة دون اتصال."
                    isNotesOnly ->
                        "يمكنك إضافة ملاحظات شخصية أو استفسارات لأي فتوى من خلال فتح تفاصيلها وحفظها محلياً."
                    searchQuery.isNotBlank() ->
                        "لم نجد نتائج للبحث عن: \"$searchQuery\". جرب استخدام كلمات مرادفة أو تصفح الأبواب الفقهية."
                    else ->
                        "لا توجد نتائج تطابق التصنيفات المحددة حالياً."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("empty_state_reset_button")
            ) {
                Text("عرض كل الفتاوى")
            }
        }
    }
}
