package com.example

import com.example.calculator.QiblaCalculator
import com.example.calculator.ShiaPrayerCalculator
import com.example.model.MidnightMethod
import com.example.model.PrayerType
import com.example.model.ShiaCalculationMethod
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ExampleUnitTest {

    @Test
    fun testQiblaBearingFromNajaf() {
        // Najaf is north-northeast of Mecca, so Qibla bearing is south-southwest (~202°)
        val bearing = QiblaCalculator.calculateQiblaBearing(32.0259, 44.3463)
        assertTrue("Najaf Qibla bearing should be around 202°, got $bearing", bearing in 198.0f..206.0f)

        val distance = QiblaCalculator.calculateDistanceToKaabaKm(32.0259, 44.3463)
        assertTrue("Distance to Kaaba from Najaf should be approx 1300-1500km, got $distance", distance in 1200.0..1600.0)
    }

    @Test
    fun testQiblaPointingAccuracy() {
        val qibla = 217.0f
        assertTrue(QiblaCalculator.isPointingToQibla(216.0f, qibla, toleranceDegrees = 3.0f))
        assertTrue(QiblaCalculator.isPointingToQibla(219.0f, qibla, toleranceDegrees = 3.0f))
        assertFalse(QiblaCalculator.isPointingToQibla(222.0f, qibla, toleranceDegrees = 3.0f))
    }

    @Test
    fun testShiaPrayerCalculationsMaghribAfterSunset() {
        val date = LocalDate.of(2026, 4, 15)
        val schedule = ShiaPrayerCalculator.calculateDailySchedule(
            date = date,
            latitude = 32.0259,
            longitude = 44.3463,
            zoneId = ZoneId.of("Asia/Baghdad"),
            method = ShiaCalculationMethod.LEVA_QUM,
            midnightMethod = MidnightMethod.MAGHRIB_TO_FAJR
        )

        val timesMap = schedule.items.associate { it.type to it.time }

        val sunset = timesMap[PrayerType.SUNSET]
        val maghrib = timesMap[PrayerType.MAGHRIB]
        val fajr = timesMap[PrayerType.FAJR]
        val dhuhr = timesMap[PrayerType.DHUHR]
        val midnight = timesMap[PrayerType.MIDNIGHT]

        assertNotNull(sunset)
        assertNotNull(maghrib)
        assertNotNull(fajr)
        assertNotNull(dhuhr)
        assertNotNull(midnight)

        // Crucial Shia Fiqh requirement: Maghrib MUST be after Sunset (due to disappearance of eastern redness)
        assertTrue(
            "Shia Maghrib ($maghrib) must be after Sunset ($sunset)",
            maghrib!!.isAfter(sunset)
        )

        // Dhuhr must be around midday (11:30 - 13:00)
        assertTrue("Dhuhr hour should be around 12, got ${dhuhr!!.hour}", dhuhr.hour in 11..13)

        // Hijri date string should not be empty and contain 'هـ'
        assertTrue(schedule.hijriDateString.isNotEmpty())
        assertTrue(schedule.hijriDateString.contains("هـ"))
    }

    @Test
    fun testPrayerNotificationWorkInputDataKeys() {
        val type = PrayerType.MAGHRIB
        assertEquals("MAGHRIB", type.name)
        assertTrue(type.isObligatory)
        assertEquals("صلاة المغرب", type.arabicName)

        val keys = listOf(
            com.example.notifications.PrayerNotificationWorker.KEY_PRAYER_TYPE,
            com.example.notifications.PrayerNotificationWorker.KEY_FORMATTED_TIME,
            com.example.notifications.PrayerNotificationWorker.KEY_CITY_NAME
        )
        assertTrue(keys.all { it.isNotEmpty() })
    }

    @Test
    fun testAllObligatoryPrayersDefined() {
        val obligatory = PrayerType.values().filter { it.isObligatory }
        assertEquals(5, obligatory.size)
        assertTrue(obligatory.contains(PrayerType.FAJR))
        assertTrue(obligatory.contains(PrayerType.DHUHR))
        assertTrue(obligatory.contains(PrayerType.ASR))
        assertTrue(obligatory.contains(PrayerType.MAGHRIB))
        assertTrue(obligatory.contains(PrayerType.ISHA))
    }
}
