package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FatwaCategory
import com.example.model.RulingType

@Composable
fun CategoryFilterBar(
    selectedCategory: FatwaCategory?,
    onCategorySelected: (FatwaCategory?) -> Unit,
    availableSubCategories: List<String>,
    selectedSubCategory: String?,
    onSubCategorySelected: (String?) -> Unit,
    selectedRulingType: RulingType?,
    onRulingTypeSelected: (RulingType?) -> Unit,
    onlyBookmarked: Boolean,
    onToggleOnlyBookmarked: () -> Unit,
    bookmarkedCount: Int,
    onlyWithNotes: Boolean = false,
    onToggleOnlyNotes: () -> Unit = {},
    notesCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_filter_bar")
    ) {
        // Main Category Tabs / Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" Chip
            FilterChip(
                selected = selectedCategory == null && !onlyBookmarked && !onlyWithNotes,
                onClick = {
                    if (onlyBookmarked) onToggleOnlyBookmarked()
                    if (onlyWithNotes) onToggleOnlyNotes()
                    onCategorySelected(null)
                },
                label = { Text("كافة الأبواب", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("filter_category_all")
            )

            // Bookmarked Only Chip
            FilterChip(
                selected = onlyBookmarked,
                onClick = onToggleOnlyBookmarked,
                label = {
                    Text(
                        text = if (bookmarkedCount > 0) "المحفوظات ($bookmarkedCount)" else "المحفوظات",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (onlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.testTag("filter_category_bookmarked")
            )

            // Personal Notes Filter Chip
            FilterChip(
                selected = onlyWithNotes,
                onClick = onToggleOnlyNotes,
                label = {
                    Text(
                        text = if (notesCount > 0) "ملاحظاتي ($notesCount)" else "ملاحظاتي",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier.testTag("filter_category_notes")
            )

            // Category Chips (Worship, Transactions, Marriage, Contemporary, Food)
            FatwaCategory.entries.forEach { category ->
                val isSelected = selectedCategory == category && !onlyBookmarked && !onlyWithNotes
                val icon = getCategoryIcon(category)

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (onlyBookmarked) onToggleOnlyBookmarked()
                        if (onlyWithNotes) onToggleOnlyNotes()
                        onCategorySelected(if (isSelected) null else category)
                    },
                    label = { Text(category.titleArabic, fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("filter_category_${category.id}")
                )
            }
        }

        // Subcategory Chips Row (Displayed only when a main category is selected)
        AnimatedVisibility(
            visible = selectedCategory != null && availableSubCategories.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "الأبواب التفصيلية:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // All subcategories chip
                    FilterChip(
                        selected = selectedSubCategory == null,
                        onClick = { onSubCategorySelected(null) },
                        label = { Text("الكل", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("filter_subcat_all")
                    )

                    availableSubCategories.forEach { subCat ->
                        val isSelected = selectedSubCategory == subCat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSubCategorySelected(if (isSelected) null else subCat) },
                            label = { Text(subCat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("filter_subcat_${subCat.hashCode()}")
                        )
                    }
                }
            }
        }

        // Quick Ruling Type Chips (e.g. واجب، جائز، احتياط وجوبي، حرام)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "نوع الحكم:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )

            RulingType.entries.forEach { ruling ->
                val isSelected = selectedRulingType == ruling
                FilterChip(
                    selected = isSelected,
                    onClick = { onRulingTypeSelected(if (isSelected) null else ruling) },
                    label = { Text(ruling.labelArabic, fontSize = 11.sp) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    } else null,
                    modifier = Modifier.testTag("filter_ruling_${ruling.name}")
                )
            }
        }
    }
}

private fun getCategoryIcon(category: FatwaCategory): ImageVector {
    return when (category) {
        FatwaCategory.WORSHIP -> Icons.Default.Mosque
        FatwaCategory.TRANSACTIONS -> Icons.Default.AccountBalance
        FatwaCategory.MARRIAGE -> Icons.Default.Favorite
        FatwaCategory.CONTEMPORARY -> Icons.Default.MedicalServices
        FatwaCategory.FOOD_DRINKS -> Icons.Default.Restaurant
    }
}
