package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.PrayerType

object PrayerNotificationHelper {

    const val CHANNEL_ID = "shia_prayer_notifications_channel"
    private const val CHANNEL_NAME = "أوقات الصلاة والأذان (الفقه الجعفري)"
    private const val CHANNEL_DESCRIPTION = "تنبيهات وإشعارات دخول أوقات الصلاة اليومية وفق الفقه الإمامي الشيعي"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.parseColor("#1B6B53") // Islamic Emerald
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 600)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun canPostNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerType: PrayerType,
        formattedTime: String,
        cityName: String = ""
    ) {
        createNotificationChannel(context)

        if (!canPostNotifications(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_tab", 1) // Prayer times tab
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title = when (prayerType) {
            PrayerType.IMSAK -> "حان الآن وقت الإمساك"
            PrayerType.FAJR -> "حان الآن موعد صلاة الصبح (الفجر)"
            PrayerType.SUNRISE -> "شروق الشمس"
            PrayerType.DHUHR -> "حان الآن موعد صلاة الظهر"
            PrayerType.ASR -> "حان الآن موعد صلاة العصر"
            PrayerType.SUNSET -> "غروب قرص الشمس"
            PrayerType.MAGHRIB -> "حان الآن موعد صلاة المغرب (زوال الحمرة)"
            PrayerType.ISHA -> "حان الآن موعد صلاة العشاء"
            PrayerType.MIDNIGHT -> "حان الآن منتصف الليل الشرعي"
        }

        val spiritualDhikr = when (prayerType) {
            PrayerType.DHUHR, PrayerType.ASR, PrayerType.MAGHRIB, PrayerType.ISHA, PrayerType.FAJR ->
                "حي على الصلاة، حي على الفلاح، حي على خير العمل"
            PrayerType.MAGHRIB ->
                "زالت الحمرة المشرقية، تقبل الله أعمالكم وطاعاتكم"
            PrayerType.IMSAK ->
                "أمسكوا يرحمكم الله - اقترب أذان الفجر"
            PrayerType.MIDNIGHT ->
                "نهاية وقت أداء صلاتي المغرب والعشاء، ووقت نافلة الليل"
            else ->
                "ألا بذكر الله تطمئن القلوب"
        }

        val locationSuffix = if (cityName.isNotBlank()) " في $cityName" else ""
        val contentText = "الساعة $formattedTime$locationSuffix - $spiritualDhikr"

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$contentText\n${prayerType.descriptionAr}")
                    .setSummaryText("الفقه الجعفري الإمامي")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400, 200, 600))
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(prayerType.ordinal + 100, notificationBuilder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun showTestNotification(context: Context) {
        createNotificationChannel(context)
        if (!canPostNotifications(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("تنبيه تجريبي: مواقيت الصلاة")
            .setContentText("إشعارات أوقات الصلاة وفق الفقه الشيعي تعمل بنجاح!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("تم ضبط وتفعيل منبه أوقات الصلاة عبر WorkManager بنجاح. ستصلك التنبيهات في وقت كل صلاة تلقائياً.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(999, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
