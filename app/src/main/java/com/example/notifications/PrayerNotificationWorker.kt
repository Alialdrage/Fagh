package com.example.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.model.PrayerType

class PrayerNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PRAYER_TYPE = "prayer_type"
        const val KEY_FORMATTED_TIME = "formatted_time"
        const val KEY_CITY_NAME = "city_name"
    }

    override suspend fun doWork(): Result {
        val prayerTypeName = inputData.getString(KEY_PRAYER_TYPE) ?: return Result.failure()
        val formattedTime = inputData.getString(KEY_FORMATTED_TIME) ?: ""
        val cityName = inputData.getString(KEY_CITY_NAME) ?: ""

        val prayerType = try {
            PrayerType.valueOf(prayerTypeName)
        } catch (e: Exception) {
            return Result.failure()
        }

        PrayerNotificationHelper.showPrayerNotification(
            context = applicationContext,
            prayerType = prayerType,
            formattedTime = formattedTime,
            cityName = cityName
        )

        return Result.success()
    }
}
