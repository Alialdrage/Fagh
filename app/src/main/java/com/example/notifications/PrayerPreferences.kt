package com.example.notifications

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MidnightMethod
import com.example.model.PrayerType
import com.example.model.ShiaCalculationMethod

class PrayerPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "shia_prayer_prefs"
        private const val KEY_GLOBAL_NOTIFICATIONS = "global_notifications_enabled"
        private const val KEY_PREFIX_PRAYER_ENABLED = "prayer_enabled_"
        private const val KEY_LATITUDE = "pref_latitude"
        private const val KEY_LONGITUDE = "pref_longitude"
        private const val KEY_CITY_NAME = "pref_city_name"
        private const val KEY_CALC_METHOD = "pref_calc_method"
        private const val KEY_MIDNIGHT_METHOD = "pref_midnight_method"
        private const val KEY_PREFIX_OFFSET = "pref_offset_"
    }

    var isGlobalNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_GLOBAL_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_GLOBAL_NOTIFICATIONS, value).apply()

    fun isPrayerNotificationEnabled(type: PrayerType): Boolean {
        // By default, obligatory prayers are enabled, optional ones disabled
        val defaultEnabled = type.isObligatory
        return prefs.getBoolean(KEY_PREFIX_PRAYER_ENABLED + type.name, defaultEnabled)
    }

    fun setPrayerNotificationEnabled(type: PrayerType, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PREFIX_PRAYER_ENABLED + type.name, enabled).apply()
    }

    fun saveLocation(lat: Double, lng: Double, cityName: String) {
        prefs.edit()
            .putFloat(KEY_LATITUDE, lat.toFloat())
            .putFloat(KEY_LONGITUDE, lng.toFloat())
            .putString(KEY_CITY_NAME, cityName)
            .apply()
    }

    fun getLatitude(): Double = prefs.getFloat(KEY_LATITUDE, 32.0259f).toDouble()
    fun getLongitude(): Double = prefs.getFloat(KEY_LONGITUDE, 44.3463f).toDouble()
    fun getCityName(): String = prefs.getString(KEY_CITY_NAME, "النجف الأشرف") ?: "النجف الأشرف"

    fun saveMethod(method: ShiaCalculationMethod) {
        prefs.edit().putString(KEY_CALC_METHOD, method.name).apply()
    }

    fun getMethod(): ShiaCalculationMethod {
        val name = prefs.getString(KEY_CALC_METHOD, ShiaCalculationMethod.LEVA_QUM.name)
        return try {
            ShiaCalculationMethod.valueOf(name ?: ShiaCalculationMethod.LEVA_QUM.name)
        } catch (e: Exception) {
            ShiaCalculationMethod.LEVA_QUM
        }
    }

    fun saveMidnightMethod(method: MidnightMethod) {
        prefs.edit().putString(KEY_MIDNIGHT_METHOD, method.name).apply()
    }

    fun getMidnightMethod(): MidnightMethod {
        val name = prefs.getString(KEY_MIDNIGHT_METHOD, MidnightMethod.MAGHRIB_TO_FAJR.name)
        return try {
            MidnightMethod.valueOf(name ?: MidnightMethod.MAGHRIB_TO_FAJR.name)
        } catch (e: Exception) {
            MidnightMethod.MAGHRIB_TO_FAJR
        }
    }

    fun saveOffset(type: PrayerType, minutes: Int) {
        prefs.edit().putInt(KEY_PREFIX_OFFSET + type.name, minutes).apply()
    }

    fun getOffset(type: PrayerType): Int {
        return prefs.getInt(KEY_PREFIX_OFFSET + type.name, 0)
    }

    fun getAllOffsets(): Map<PrayerType, Int> {
        val result = mutableMapOf<PrayerType, Int>()
        for (type in PrayerType.values()) {
            val offset = getOffset(type)
            if (offset != 0) {
                result[type] = offset
            }
        }
        return result
    }
}
