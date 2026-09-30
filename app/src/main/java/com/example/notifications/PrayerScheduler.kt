package com.example.notifications

import android.content.Context
import androidx.work.*
import com.example.calculator.ShiaPrayerCalculator
import com.example.model.PrayerType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object PrayerScheduler {

    const val TAG_PRAYER_WORK = "tag_shia_prayer_notification"
    private const val PERIODIC_REFRESH_WORK_NAME = "periodic_shia_prayer_reschedule"

    /**
     * Reschedules all upcoming prayer notification works based on current settings and coordinates.
     */
    fun scheduleAllPrayers(context: Context) {
        val prefs = PrayerPreferences(context)
        val workManager = WorkManager.getInstance(context)

        // If global notifications are disabled, cancel all scheduled prayer works
        if (!prefs.isGlobalNotificationEnabled) {
            workManager.cancelAllWorkByTag(TAG_PRAYER_WORK)
            return
        }

        val lat = prefs.getLatitude()
        val lng = prefs.getLongitude()
        val cityName = prefs.getCityName()
        val method = prefs.getMethod()
        val midnightMethod = prefs.getMidnightMethod()
        val offsets = prefs.getAllOffsets()

        val zoneId = ZoneId.systemDefault()
        val now = LocalDateTime.now(zoneId)
        val today = LocalDate.now(zoneId)
        val tomorrow = today.plusDays(1)

        val todaySchedule = ShiaPrayerCalculator.calculateDailySchedule(
            date = today,
            latitude = lat,
            longitude = lng,
            zoneId = zoneId,
            method = method,
            midnightMethod = midnightMethod,
            minuteOffsets = offsets
        )

        val tomorrowSchedule = ShiaPrayerCalculator.calculateDailySchedule(
            date = tomorrow,
            latitude = lat,
            longitude = lng,
            zoneId = zoneId,
            method = method,
            midnightMethod = midnightMethod,
            minuteOffsets = offsets
        )

        // For each prayer type, schedule the next upcoming occurrence
        for (prayerType in PrayerType.values()) {
            if (!prefs.isPrayerNotificationEnabled(prayerType)) {
                // If this specific prayer is disabled by user, cancel its work
                workManager.cancelUniqueWork("prayer_notif_${prayerType.name}")
                continue
            }

            val todayItem = todaySchedule.items.firstOrNull { it.type == prayerType }
            val tomorrowItem = tomorrowSchedule.items.firstOrNull { it.type == prayerType }

            if (todayItem == null) continue

            val todayDateTime = LocalDateTime.of(today, todayItem.time)
            val (targetDateTime, targetFormattedTime) = if (todayDateTime.isAfter(now)) {
                todayDateTime to todayItem.formattedTime
            } else if (tomorrowItem != null) {
                LocalDateTime.of(tomorrow, tomorrowItem.time) to tomorrowItem.formattedTime
            } else {
                continue
            }

            val targetEpochMillis = targetDateTime.atZone(zoneId).toInstant().toEpochMilli()
            val nowEpochMillis = now.atZone(zoneId).toInstant().toEpochMilli()
            val delayMillis = targetEpochMillis - nowEpochMillis

            if (delayMillis > 0) {
                val inputData = workDataOf(
                    PrayerNotificationWorker.KEY_PRAYER_TYPE to prayerType.name,
                    PrayerNotificationWorker.KEY_FORMATTED_TIME to targetFormattedTime,
                    PrayerNotificationWorker.KEY_CITY_NAME to cityName
                )

                val workRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .addTag(TAG_PRAYER_WORK)
                    .addTag("prayer_${prayerType.name}")
                    .setInputData(inputData)
                    .build()

                workManager.enqueueUniqueWork(
                    "prayer_notif_${prayerType.name}",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
        }

        // Also ensure periodic background refresh worker is running
        schedulePeriodicRefreshWorker(context)
    }

    /**
     * Schedules a periodic background job that ensures prayer notifications are always refreshed
     * every 12 hours even if user hasn't opened the app.
     */
    fun schedulePeriodicRefreshWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val periodicRequest = PeriodicWorkRequestBuilder<DailyPrayerRefreshWorker>(
            12, TimeUnit.HOURS,
            30, TimeUnit.MINUTES // Flex interval
        ).build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_REFRESH_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun cancelAllPrayers(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(TAG_PRAYER_WORK)
    }
}
