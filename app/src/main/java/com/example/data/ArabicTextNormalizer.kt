package com.example.data

object ArabicTextNormalizer {
    private val TASHKEEL_REGEX = Regex("[\\u0617-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06ED]")

    /**
     * Normalizes Arabic text for flexible matching:
     * - Strips tashkeel (harakat / diacritics)
     * - Normalizes Alif variants (أ، إ، آ، ٱ -> ا)
     * - Normalizes Yaa / Alif Maqsura (ى -> ي)
     * - Normalizes Taa Marbuta (ة -> ه)
     * - Trims excess spaces and lowercases
     */
    fun normalize(input: String): String {
        if (input.isBlank()) return ""
        var result = TASHKEEL_REGEX.replace(input, "")
        result = result
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ٱ', 'ا')
            .replace('ى', 'ي')
            .replace('ة', 'ه')
            .replace('ؤ', 'و')
            .replace('ئ', 'ي')
            .lowercase()
            .trim()
        return result
    }

    /**
     * Checks if target text contains the search query with normalized matching
     */
    fun matches(target: String, query: String): Boolean {
        val normalizedTarget = normalize(target)
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isBlank()) return true
        
        // Multi-word search: all query terms should match
        val queryWords = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        return queryWords.all { word -> normalizedTarget.contains(word) }
    }
}
