package com.example.calculator

import com.example.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

object ShiaPrayerCalculator {

    /**
     * Calculates complete daily Shia prayer times for a given date, coordinates and timezone.
     */
    fun calculateDailySchedule(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId,
        method: ShiaCalculationMethod = ShiaCalculationMethod.LEVA_QUM,
        midnightMethod: MidnightMethod = MidnightMethod.MAGHRIB_TO_FAJR,
        minuteOffsets: Map<PrayerType, Int> = emptyMap()
    ): DailyPrayerSchedule {
        val zoneOffsetHours = zoneId.rules.getOffset(date.atStartOfDay()).totalSeconds / 3600.0

        val timesMap = calculateRawTimes(date, latitude, longitude, zoneOffsetHours, method)
        val nextDayTimesMap = calculateRawTimes(date.plusDays(1), latitude, longitude, zoneOffsetHours, method)

        // Calculate Midnight
        val maghribHours = timesMap[PrayerType.MAGHRIB] ?: 18.0
        val sunsetHours = timesMap[PrayerType.SUNSET] ?: 17.75
        val nextFajrHours = nextDayTimesMap[PrayerType.FAJR] ?: 5.0

        val midnightBase = if (midnightMethod == MidnightMethod.MAGHRIB_TO_FAJR) maghribHours else sunsetHours
        val spanHours = (nextFajrHours + 24.0 - midnightBase)
        val midnightHours = (midnightBase + spanHours / 2.0) % 24.0

        val mutableTimes = timesMap.toMutableMap()
        mutableTimes[PrayerType.MIDNIGHT] = midnightHours

        // Apply minute adjustments and convert to LocalTime
        val now = LocalTime.now(zoneId)
        val isToday = date == LocalDate.now(zoneId)

        val prayerItems = mutableListOf<PrayerTimeItem>()
        val orderedTypes = listOf(
            PrayerType.IMSAK,
            PrayerType.FAJR,
            PrayerType.SUNRISE,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.SUNSET,
            PrayerType.MAGHRIB,
            PrayerType.ISHA,
            PrayerType.MIDNIGHT
        )

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

        for (type in orderedTypes) {
            val baseHours = mutableTimes[type] ?: 12.0
            val offsetMinutes = minuteOffsets[type] ?: 0
            val adjustedHours = baseHours + (offsetMinutes / 60.0)

            val localTime = decimalHoursToLocalTime(adjustedHours)
            val formatted = format12Hour(localTime)

            prayerItems.add(
                PrayerTimeItem(
                    type = type,
                    time = localTime,
                    formattedTime = formatted
                )
            )
        }

        // Determine current window and next prayer
        var nextPrayer: PrayerTimeItem? = null
        var remainingMillis = 0L
        var currentWindow: PrayerType? = null

        if (isToday) {
            // Find next upcoming prayer
            for (item in prayerItems) {
                if (item.time.isAfter(now)) {
                    nextPrayer = item
                    val remainingSeconds = (item.time.toSecondOfDay() - now.toSecondOfDay())
                    remainingMillis = (remainingSeconds * 1000L).coerceAtLeast(0L)
                    break
                }
            }

            // If all prayers today have passed, next is tomorrow's Fajr
            if (nextPrayer == null && prayerItems.isNotEmpty()) {
                val fajrTomorrow = prayerItems.first { it.type == PrayerType.FAJR }
                nextPrayer = fajrTomorrow
                val secUntilMidnight = (86400 - now.toSecondOfDay())
                val secAfterMidnight = fajrTomorrow.time.toSecondOfDay()
                remainingMillis = ((secUntilMidnight + secAfterMidnight) * 1000L).coerceAtLeast(0L)
            }

            // Find current prayer window
            val passedPrayers = prayerItems.filter { !it.time.isAfter(now) }
            currentWindow = passedPrayers.lastOrNull()?.type
        }

        val enrichedItems = prayerItems.map { item ->
            item.copy(
                isNext = isToday && (item.type == nextPrayer?.type),
                isCurrentWindow = isToday && (item.type == currentWindow)
            )
        }

        val hijriDateStr = calculateHijriDate(date)
        val gregorianDateStr = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()))

        return DailyPrayerSchedule(
            date = date,
            hijriDateString = hijriDateStr,
            gregorianDateString = gregorianDateStr,
            items = enrichedItems,
            nextPrayer = nextPrayer,
            remainingMillis = remainingMillis,
            currentPrayerType = currentWindow
        )
    }

    private fun calculateRawTimes(
        date: LocalDate,
        lat: Double,
        lng: Double,
        timeZoneHours: Double,
        method: ShiaCalculationMethod
    ): Map<PrayerType, Double> {
        val jd = julianDate(date.year, date.monthValue, date.dayOfMonth)
        val d = jd - 2451545.0

        // Mean solar anomaly
        val m = fixAngle(357.529 + 0.98560028 * d)
        // Mean solar longitude
        val q = fixAngle(280.459 + 0.98564736 * d)
        // Apparent solar longitude
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(m)) + 0.020 * sin(Math.toRadians(2 * m)))
        // Obliquity of ecliptic
        val e = 23.439 - 0.00000036 * d

        // Sun declination
        val sinDecl = sin(Math.toRadians(e)) * sin(Math.toRadians(l))
        val declDeg = Math.toDegrees(asin(sinDecl))

        // Right ascension in hours
        val raDeg = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))))
        val raHours = raDeg / 15.0

        // Equation of Time in hours
        val eotHours = (q / 15.0) - raHours

        // Solar Noon (Zawal / Midday)
        val solarNoon = 12.0 + timeZoneHours - (lng / 15.0) - eotHours

        // Dhuhr: Shia Ihtiyat adds 1.5 minutes to ensure solar transit has occurred
        val dhuhrHours = solarNoon + (1.5 / 60.0)

        // Sunrise & Sunset (Zenith = 90.833°)
        val sunriseDiff = hourAngleForZenith(90.833, lat, declDeg)
        val sunriseHours = solarNoon - sunriseDiff
        val sunsetHours = solarNoon + sunriseDiff

        // Fajr (Subh): Leva Institute Qum is 16.0° below horizon (zenith 106.0°)
        val fajrZenith = 90.0 + method.fajrAngle
        val fajrDiff = hourAngleForZenith(fajrZenith, lat, declDeg)
        val fajrHours = solarNoon - fajrDiff

        // Imsak: 10 minutes before Fajr
        val imsakHours = fajrHours - (10.0 / 60.0)

        // Asr: Standard shadow ratio (shadow equals object length)
        val asrZenith = asrZenithAngle(lat, declDeg)
        val asrDiff = hourAngleForZenith(asrZenith, lat, declDeg)
        val asrHours = solarNoon + asrDiff

        // Maghrib: Shia Ja'fari Maghrib occurs upon disappearance of eastern redness
        // (Leva Institute = 4.0° below horizon, Tehran = 4.5° below horizon)
        val maghribZenith = 90.0 + method.maghribAngle
        val maghribDiff = hourAngleForZenith(maghribZenith, lat, declDeg)
        val maghribHours = solarNoon + maghribDiff

        // Isha: Virtue time of Isha (14.0° below horizon)
        val ishaZenith = 90.0 + method.ishaAngle
        val ishaDiff = hourAngleForZenith(ishaZenith, lat, declDeg)
        val ishaHours = solarNoon + ishaDiff

        return mapOf(
            PrayerType.IMSAK to fixHours(imsakHours),
            PrayerType.FAJR to fixHours(fajrHours),
            PrayerType.SUNRISE to fixHours(sunriseHours),
            PrayerType.DHUHR to fixHours(dhuhrHours),
            PrayerType.ASR to fixHours(asrHours),
            PrayerType.SUNSET to fixHours(sunsetHours),
            PrayerType.MAGHRIB to fixHours(maghribHours),
            PrayerType.ISHA to fixHours(ishaHours)
        )
    }

    private fun hourAngleForZenith(zenithDeg: Double, latDeg: Double, declDeg: Double): Double {
        val latRad = Math.toRadians(latDeg)
        val declRad = Math.toRadians(declDeg)
        val zenRad = Math.toRadians(zenithDeg)

        val cosH = (cos(zenRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        val hDeg = Math.toDegrees(acos(clampedCosH))
        return hDeg / 15.0
    }

    private fun asrZenithAngle(latDeg: Double, declDeg: Double): Double {
        val delta = abs(latDeg - declDeg)
        val tanDelta = tan(Math.toRadians(delta))
        // Shadow factor = 1.0 (mislihi)
        val cotAlt = 1.0 + tanDelta
        val altDeg = Math.toDegrees(atan(1.0 / cotAlt))
        return 90.0 - altDeg
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(deg: Double): Double {
        val a = deg % 360.0
        return if (a < 0) a + 360.0 else a
    }

    private fun fixHours(hours: Double): Double {
        val h = hours % 24.0
        return if (h < 0) h + 24.0 else h
    }

    private fun decimalHoursToLocalTime(decHours: Double): LocalTime {
        val normalized = fixHours(decHours)
        val totalSeconds = (normalized * 3600.0).roundToInt()
        val hours = (totalSeconds / 3600) % 24
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return LocalTime.of(hours, minutes, seconds)
    }

    private fun format12Hour(time: LocalTime): String {
        val hour12 = when (time.hour) {
            0 -> 12
            in 1..12 -> time.hour
            else -> time.hour - 12
        }
        val amPm = if (time.hour < 12) "ص" else "م"
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, time.minute, amPm)
    }

    /**
     * Algorithmic Hijri Date Conversion (Tabular Islamic Calendar grounded in lunar months).
     */
    fun calculateHijriDate(date: LocalDate): String {
        val jd = julianDate(date.year, date.monthValue, date.dayOfMonth)
        val l = jd.toLong() - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = (((10985 - l2) / 5316).toInt()) * ((50 * l2) / 17719).toInt() +
                ((l2 / 5670).toInt()) * ((43 * l2) / 15238).toInt()
        val l3 = l2 - (((30 - j) / 15).toInt()) * ((17719 * j) / 50).toInt() -
                ((j / 16).toInt()) * ((15238 * j) / 43).toInt() + 29
        val month = ((24 * l3) / 709).toInt()
        val day = (l3 - ((709 * month) / 24).toInt()).toInt()
        val year = (30 * n + j - 30).toInt()

        val hijriMonthsAr = listOf(
            "محرم الحرام", "صفر الخير", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب الأصب", "شعبان المعظم",
            "شهر رمضان المبارك", "شوال المكرم", "ذو القعدة الحرام", "ذو الحجة الحرام"
        )

        val monthName = if (month in 1..12) hijriMonthsAr[month - 1] else "شهر $month"
        return "$day $monthName $year هـ"
    }
}
