package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Fatwa
import com.example.model.FatwaCategory
import com.example.model.RulingType

/**
 * Room database entity storing cached religious rulings and fatwas for offline access.
 */
@Entity(tableName = "fatwas")
data class FatwaEntity(
    @PrimaryKey
    val id: String,
    val questionNumber: Int,
    val title: String,
    val question: String,
    val answer: String,
    val category: String,
    val subCategory: String,
    val rulingType: String,
    val sourceBook: String,
    val tags: String = "",
    val isBookmarked: Boolean = false,
    val isOfflineCached: Boolean = true,
    val cachedTimestamp: Long = System.currentTimeMillis(),
    val userNotes: String = ""
) {
    fun toFatwa(): Fatwa {
        return Fatwa(
            id = id,
            questionNumber = questionNumber,
            title = title,
            question = question,
            answer = answer,
            category = try {
                FatwaCategory.valueOf(category)
            } catch (e: Exception) {
                FatwaCategory.WORSHIP
            },
            subCategory = subCategory,
            rulingType = try {
                RulingType.valueOf(rulingType)
            } catch (e: Exception) {
                RulingType.WAJIB
            },
            sourceBook = sourceBook,
            tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            isBookmarked = isBookmarked,
            isOfflineCached = isOfflineCached,
            cachedTimestamp = cachedTimestamp,
            userNotes = userNotes
        )
    }

    companion object {
        fun fromFatwa(fatwa: Fatwa): FatwaEntity {
            return FatwaEntity(
                id = fatwa.id,
                questionNumber = fatwa.questionNumber,
                title = fatwa.title,
                question = fatwa.question,
                answer = fatwa.answer,
                category = fatwa.category.name,
                subCategory = fatwa.subCategory,
                rulingType = fatwa.rulingType.name,
                sourceBook = fatwa.sourceBook,
                tags = fatwa.tags.joinToString(","),
                isBookmarked = fatwa.isBookmarked,
                isOfflineCached = fatwa.isOfflineCached,
                cachedTimestamp = fatwa.cachedTimestamp,
                userNotes = fatwa.userNotes
            )
        }
    }
}
