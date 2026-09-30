package com.example.calculator

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the bearing in degrees from North (0° = North, 90° = East, 180° = South, 270° = West)
     * pointing towards the Kaaba in Mecca from the given latitude and longitude.
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val dLng = Math.toRadians(KAABA_LONGITUDE - userLng)

        val y = sin(dLng)
        val x = cos(lat1) * tan(lat2) - sin(lat1) * cos(dLng)

        var initialBearing = Math.toDegrees(atan2(y, x))
        initialBearing = (initialBearing + 360.0) % 360.0
        return initialBearing.toFloat()
    }

    /**
     * Calculates the great circle distance to the Kaaba in kilometers.
     */
    fun calculateDistanceToKaabaKm(userLat: Double, userLng: Double): Double {
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
        val dLng = Math.toRadians(KAABA_LONGITUDE - userLng)

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Calculates the relative angle difference between device heading and Qibla bearing.
     * Negative means Qibla is to the left, positive means to the right.
     * Normalized between -180° and +180°.
     */
    fun getRelativeQiblaOffset(deviceHeading: Float, qiblaBearing: Float): Float {
        var diff = qiblaBearing - deviceHeading
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        return diff
    }

    /**
     * Checks if the device is pointing to the Kaaba within a specified tolerance angle (e.g. ±3 degrees).
     */
    fun isPointingToQibla(deviceHeading: Float, qiblaBearing: Float, toleranceDegrees: Float = 3.0f): Boolean {
        val diff = abs(getRelativeQiblaOffset(deviceHeading, qiblaBearing))
        return diff <= toleranceDegrees
    }

    /**
     * Returns compass cardinal direction abbreviation in Arabic and English.
     */
    fun getCompassDirectionText(bearing: Float): Pair<String, String> {
        val normalized = (bearing % 360f + 360f) % 360f
        return when {
            normalized in 348.75..360.0 || normalized in 0.0..11.25 -> "شمال" to "N"
            normalized in 11.25..33.75 -> "شمال شمال شرق" to "NNE"
            normalized in 33.75..56.25 -> "شمال شرق" to "NE"
            normalized in 56.25..78.75 -> "شرق شمال شرق" to "ENE"
            normalized in 78.75..101.25 -> "شرق" to "E"
            normalized in 101.25..123.75 -> "شرق جنوب شرق" to "ESE"
            normalized in 123.75..146.25 -> "جنوب شرق" to "SE"
            normalized in 146.25..168.75 -> "جنوب جنوب شرق" to "SSE"
            normalized in 168.75..191.25 -> "جنوب" to "S"
            normalized in 191.25..213.75 -> "جنوب جنوب غرب" to "SSW"
            normalized in 213.75..236.25 -> "جنوب غرب" to "SW"
            normalized in 236.25..258.75 -> "غرب جنوب غرب" to "WSW"
            normalized in 258.75..281.25 -> "غرب" to "W"
            normalized in 281.25..303.75 -> "غرب شمال غرب" to "WNW"
            normalized in 303.75..326.25 -> "شمال غرب" to "NW"
            else -> "شمال شمال غرب" to "NNW"
        }
    }
}
