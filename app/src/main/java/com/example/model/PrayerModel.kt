package com.example.model

import java.time.LocalDate
import java.time.LocalTime

enum class PrayerType(
    val arabicName: String,
    val englishName: String,
    val isObligatory: Boolean,
    val descriptionAr: String,
    val descriptionEn: String
) {
    IMSAK(
        arabicName = "الإمساك",
        englishName = "Imsak",
        isObligatory = false,
        descriptionAr = "قبل أذان الصبح بـ 10 دقائق احتياطاً للصيام",
        descriptionEn = "10 minutes before Fajr as precaution for fasting"
    ),
    FAJR(
        arabicName = "صلاة الصبح",
        englishName = "Fajr (Subh)",
        isObligatory = true,
        descriptionAr = "طلوع الفجر الصادق (زاوية 16° بحسب معهد لواء - قم)",
        descriptionEn = "True dawn (16° depression angle according to Leva Institute - Qum)"
    ),
    SUNRISE(
        arabicName = "شروق الشمس",
        englishName = "Sunrise",
        isObligatory = false,
        descriptionAr = "شروق قرص الشمس ونهاية وقت فضيلة الصبح",
        descriptionEn = "Sun disk emerges above the horizon"
    ),
    DHUHR(
        arabicName = "صلاة الظهر",
        englishName = "Dhuhr",
        isObligatory = true,
        descriptionAr = "زوال الشمس وميلها عن خط نصف النهار",
        descriptionEn = "Solar noon when sun passes the meridian"
    ),
    ASR(
        arabicName = "صلاة العصر",
        englishName = "Asr",
        isObligatory = true,
        descriptionAr = "دخول وقت فضيلة العصر عند مساواة ظل الشاخص لمثله",
        descriptionEn = "Virtue time of Asr (standard shadow length)"
    ),
    SUNSET(
        arabicName = "غروب الشمس",
        englishName = "Sunset",
        isObligatory = false,
        descriptionAr = "سقوط قرص الشمس عن الأفق الظاهري (ليس وقت المغرب الشرعي)",
        descriptionEn = "Astronomical sunset (not the Ja'fari Maghrib time)"
    ),
    MAGHRIB(
        arabicName = "صلاة المغرب",
        englishName = "Maghrib",
        isObligatory = true,
        descriptionAr = "زوال الحمرة المشرقية من فوق الرأس (زاوية 4° تحت الأفق بحسب الفقه الجعفري)",
        descriptionEn = "Disappearance of eastern redness overhead (4° below horizon in Ja'fari fiqh)"
    ),
    ISHA(
        arabicName = "صلاة العشاء",
        englishName = "Isha",
        isObligatory = true,
        descriptionAr = "دخول وقت فضيلة العشاء بعد غياب الشفق الأحمر",
        descriptionEn = "Virtue time of Isha after red western twilight fades"
    ),
    MIDNIGHT(
        arabicName = "منتصف الليل الشرعي",
        englishName = "Islamic Midnight",
        isObligatory = false,
        descriptionAr = "منتصف الوقت بين المغرب والفجر؛ انتهاء وقت أداء صلاتي المغرب والعشاء",
        descriptionEn = "Midpoint between Maghrib and Fajr; end of time for Maghrib & Isha"
    )
}

enum class ShiaCalculationMethod(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val fajrAngle: Double,
    val maghribAngle: Double,
    val ishaAngle: Double,
    val notes: String
) {
    LEVA_QUM(
        id = "LEVA_QUM",
        arabicName = "معهد لواء للأبحاث - قم المقدسة",
        englishName = "Leva Institute, Qum (Sistani / Khamenei standard)",
        fajrAngle = 16.0,
        maghribAngle = 4.0,
        ishaAngle = 14.0,
        notes = "المعتمد لدى مراجع النجف الأشرف وقم المقدسة (الفجر 16°، المغرب 4° بعد الغروب)"
    ),
    TEHRAN_GEOPHYSICS(
        id = "TEHRAN_GEOPHYSICS",
        arabicName = "معهد الجيوفيزياء - جامعة طهران",
        englishName = "Institute of Geophysics, Tehran University",
        fajrAngle = 17.7,
        maghribAngle = 4.5,
        ishaAngle = 14.0,
        notes = "معتمد في التقاويم الرسمية الإيرانية (الفجر 17.7°، المغرب 4.5°)"
    )
}

enum class MidnightMethod(
    val arabicName: String,
    val englishName: String
) {
    MAGHRIB_TO_FAJR(
        arabicName = "من المغرب إلى الفجر (المشهور)",
        englishName = "From Maghrib to Fajr (Predominant Shia view)"
    ),
    SUNSET_TO_FAJR(
        arabicName = "من الغروب إلى الفجر",
        englishName = "From Sunset to Fajr"
    )
}

data class PrayerTimeItem(
    val type: PrayerType,
    val time: LocalTime,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isCurrentWindow: Boolean = false
)

data class DailyPrayerSchedule(
    val date: LocalDate,
    val hijriDateString: String,
    val gregorianDateString: String,
    val items: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val remainingMillis: Long,
    val currentPrayerType: PrayerType?
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryName: String = "",
    val isGps: Boolean = true
)

enum class SensorAccuracyLevel(val labelAr: String, val labelEn: String) {
    HIGH("دقة عالية", "High Accuracy"),
    MEDIUM("دقة متوسطة", "Medium Accuracy"),
    LOW("دقة منخفضة (يرجى تحريك الهاتف بشكل 8)", "Low Accuracy (Calibrate)"),
    UNRELIABLE("غير موثوق (يرجى المعايرة)", "Unreliable (Calibrate)")
}

data class CityPreset(
    val nameAr: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String,
    val isHolySanctuary: Boolean = false
)

object HolyCitiesPresets {
    val CITIES = listOf(
        CityPreset("النجف الأشرف", "Najaf, Iraq", 32.0259, 44.3463, "Asia/Baghdad", true),
        CityPreset("كربلاء المقدسة", "Karbala, Iraq", 32.6160, 44.0249, "Asia/Baghdad", true),
        CityPreset("مشهد المقدسة", "Mashhad, Iran", 36.2972, 59.6067, "Asia/Tehran", true),
        CityPreset("قم المقدسة", "Qom, Iran", 34.6401, 50.8764, "Asia/Tehran", true),
        CityPreset("مكة المكرمة", "Mecca, Saudi Arabia", 21.4225, 39.8262, "Asia/Riyadh", true),
        CityPreset("المدينة المنورة", "Medina, Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh", true),
        CityPreset("الكاظمية - بغداد", "Kadhimiya / Baghdad, Iraq", 33.3800, 44.3400, "Asia/Baghdad", true),
        CityPreset("سامراء المشرفة", "Samarra, Iraq", 34.1983, 43.8742, "Asia/Baghdad", true),
        CityPreset("بيروت", "Beirut, Lebanon", 33.8938, 35.5018, "Asia/Beirut", false),
        CityPreset("دمشق - السيدة زينب", "Damascus / Sayyida Zaynab, Syria", 33.4474, 36.3392, "Asia/Damascus", true),
        CityPreset("المنامة", "Manama, Bahrain", 26.2285, 50.5860, "Asia/Bahrain", false),
        CityPreset("الكويت", "Kuwait City, Kuwait", 29.3759, 47.9774, "Asia/Kuwait", false),
        CityPreset("مسقط", "Muscat, Oman", 23.5880, 58.3829, "Asia/Muscat", false),
        CityPreset("طهران", "Tehran, Iran", 35.6892, 51.3890, "Asia/Tehran", false),
        CityPreset("الرياض", "Riyadh, Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh", false),
        CityPreset("القاهرة", "Cairo, Egypt", 30.0444, 31.2357, "Africa/Cairo", false),
        CityPreset("دبي", "Dubai, UAE", 25.2048, 55.2708, "Asia/Dubai", false),
        CityPreset("لندن", "London, UK", 51.5074, -0.1278, "Europe/London", false),
        CityPreset("ديربورن / ديترويت", "Dearborn / Detroit, USA", 42.3223, -83.1763, "America/Detroit", false),
        CityPreset("سيدني", "Sydney, Australia", -33.8688, 151.2093, "Australia/Sydney", false)
    )
}
