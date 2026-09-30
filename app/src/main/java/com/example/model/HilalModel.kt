package com.example.model

/**
 * Data structures for Hilal (Crescent Moon) Sightings & Forecasts
 * according to the guidelines of Grand Ayatollah Sayyid Ali al-Sistani's office (Najaf al-Ashraf).
 */

enum class HilalVisibilityStatus(
    val titleArabic: String,
    val descriptionArabic: String,
    val colorHex: Long,
    val sistaniRulingNote: String
) {
    VISIBLE_NAKED_EYE(
        titleArabic = "مرئي بالعين المجردة بسهولة",
        descriptionArabic = "الهلال مرتفع ونسبة إضاءته كافية ومكثه طويل يتيح الرؤية الاعتيادية.",
        colorHex = 0xFF2E7D32, // Dark Green
        sistaniRulingNote = "يثبت دخول الشهر الشرعي في هذا الأفق والآفاق المشتركة معه في الليل."
    ),
    VISIBLE_IF_CLEAR(
        titleArabic = "ممكن بالعين المجردة مع صفاء الجو",
        descriptionArabic = "يمكن رؤيته بالعين المجردة إذا كان الأفق نقياً تماماً وخالياً من الغبار والغيوم والرطوبة.",
        colorHex = 0xFF0D6E6E, // Teal
        sistaniRulingNote = "يثبت الشهر إذا تيسرت الرؤية الحسية للمستهلين بالعين المجردة."
    ),
    TELESCOPE_ONLY(
        titleArabic = "ممكن بالعين المسلحة (التلسكوب) فقط",
        descriptionArabic = "الهلال دقيق ومكثه أو ارتفاعه منخفض ولا يرى بالعين المجردة بل عبر المراصد والتلسكوبات فقط.",
        colorHex = 0xFFD87A00, // Amber / Warm Orange
        sistaniRulingNote = "لا يثبت دخول الشهر الشرعي بالرؤية المسلحة عند سماحة السيد، بل يشترط الرؤية بالعين المجردة."
    ),
    IMPOSSIBLE(
        titleArabic = "غير ممكنة الرؤية",
        descriptionArabic = "القمر يغرب قبل الشمس أو معها، أو يغرب بعدها بمدة يسيرة جداً لا تسمح بالرؤية إطلاقاً.",
        colorHex = 0xFFC62828, // Red
        sistaniRulingNote = "تتم عدة الشهر ثلاثين يوماً ويكون اليوم التالي متمماً للشهر السابق."
    )
}

data class HilalCityData(
    val cityName: String,
    val sunsetTime: String,
    val moonsetTime: String,
    val stayDurationMinutes: Int,
    val altitudeDegrees: Double,
    val illuminationPercent: Double,
    val elongationAngle: Double,
    val visibilityStatus: HilalVisibilityStatus
)

data class IslamicEvent(
    val day: Int,
    val title: String,
    val description: String,
    val isMajor: Boolean = false
)

data class HilalMonthRecord(
    val id: String,
    val monthIndex: Int, // 1 to 12
    val monthNameArabic: String,
    val monthTitleFull: String,
    val hijriYear: Int,
    val conjunctionDateTime: String,
    val observationEvening: String,
    val expectedFirstDay: String,
    val moonAgeHours: Double,
    val primaryCity: HilalCityData,
    val otherCities: List<HilalCityData> = emptyList(),
    val sistaniFiqhStatement: String,
    val events: List<IslamicEvent> = emptyList()
)

data class HilalFiqhRule(
    val id: String,
    val title: String,
    val questionOrTopic: String,
    val ruling: String,
    val source: String
)
