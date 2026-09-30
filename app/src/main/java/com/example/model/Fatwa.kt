package com.example.model

/**
 * Representation of a Fatwa (Islamic legal ruling) by Grand Ayatollah Sayyid Ali al-Sistani.
 */
data class Fatwa(
    val id: String,
    val questionNumber: Int,
    val title: String,
    val question: String,
    val answer: String,
    val category: FatwaCategory,
    val subCategory: String,
    val rulingType: RulingType,
    val sourceBook: String,
    val tags: List<String> = emptyList(),
    val isBookmarked: Boolean = false,
    val isOfflineCached: Boolean = true,
    val cachedTimestamp: Long = System.currentTimeMillis(),
    val userNotes: String = ""
)

enum class FatwaCategory(
    val id: String,
    val titleArabic: String,
    val subtitle: String,
    val iconName: String
) {
    WORSHIP(
        id = "worship",
        titleArabic = "العبادات",
        subtitle = "الصلاة، الصوم، الطهارة، الحج، الخمس",
        iconName = "mosque"
    ),
    TRANSACTIONS(
        id = "transactions",
        titleArabic = "المعاملات والتجارة",
        subtitle = "البيع، البنوك، الديون، العمل والوظائف",
        iconName = "account_balance"
    ),
    MARRIAGE(
        id = "marriage",
        titleArabic = "الزواج والأسرة",
        subtitle = "عقد النكاح، حقوق الزوجين، الطلاق، الحجاب",
        iconName = "favorite"
    ),
    CONTEMPORARY(
        id = "contemporary",
        titleArabic = "المستحدثات والطبية",
        subtitle = "التلقيح، التبرع بالأعضاء، التجميل، العملات الرقمية",
        iconName = "medical_services"
    ),
    FOOD_DRINKS(
        id = "food_drinks",
        titleArabic = "الأطعمة والأشربة",
        subtitle = "الذبائح، اللحوم المستوردة، الجيلاتين، الكحول",
        iconName = "restaurant"
    )
}

enum class RulingType(
    val labelArabic: String,
    val colorHex: Long
) {
    WAJIB(
        labelArabic = "واجب شرعي",
        colorHex = 0xFF1B5E20 // Dark Green
    ),
    JAIZ(
        labelArabic = "جائز / مباح",
        colorHex = 0xFF0D6E6E // Deep Teal
    ),
    MUSTAHABB(
        labelArabic = "مستحب",
        colorHex = 0xFF2E7D32 // Green
    ),
    IHTIYAT_WUJUBI(
        labelArabic = "احتياط وجوبي",
        colorHex = 0xFFC67C00 // Amber / Warm Orange
    ),
    MAKRUH(
        labelArabic = "مكروه",
        colorHex = 0xFF795548 // Brown
    ),
    HARAM(
        labelArabic = "حرام شرعاً",
        colorHex = 0xFFB71C1C // Crimson Red
    )
}
