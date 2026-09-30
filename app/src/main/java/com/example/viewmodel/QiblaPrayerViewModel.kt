package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.QiblaCalculator
import com.example.calculator.ShiaPrayerCalculator
import com.example.location.LocationHelper
import com.example.model.*
import com.example.notifications.PrayerNotificationHelper
import com.example.notifications.PrayerPreferences
import com.example.notifications.PrayerScheduler
import com.example.sensor.CompassSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class QiblaUiState(
    val userLocation: UserLocation = UserLocation(
        latitude = 32.0259,
        longitude = 44.3463,
        cityName = "النجف الأشرف",
        countryName = "العراق",
        isGps = false
    ),
    val isLocationPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = true,
    val isLoadingLocation: Boolean = false,
    val deviceHeading: Float = 0f,
    val qiblaBearing: Float = 202.0f,
    val distanceToKaabaKm: Double = 1380.0,
    val qiblaOffsetAngle: Float = 0f,
    val isAlignedWithQibla: Boolean = false,
    val isDeviceFlat: Boolean = true,
    val sensorAccuracy: SensorAccuracyLevel = SensorAccuracyLevel.HIGH,
    val isSensorSupported: Boolean = true,
    val calculationMethod: ShiaCalculationMethod = ShiaCalculationMethod.LEVA_QUM,
    val midnightMethod: MidnightMethod = MidnightMethod.MAGHRIB_TO_FAJR,
    val selectedDate: LocalDate = LocalDate.now(),
    val prayerSchedule: DailyPrayerSchedule? = null,
    val minuteOffsets: Map<PrayerType, Int> = emptyMap(),
    val selectedCityPreset: CityPreset? = HolyCitiesPresets.CITIES.first(),
    val isGlobalNotificationEnabled: Boolean = true,
    val prayerNotificationMap: Map<PrayerType, Boolean> = emptyMap(),
    val activeTab: Int = 0 // 0 = Compass, 1 = Prayer Times, 2 = Settings
)

class QiblaPrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val compassSensorManager = CompassSensorManager(application)
    private val locationHelper = LocationHelper(application)
    private val prayerPreferences = PrayerPreferences(application)

    private val _uiState = MutableStateFlow(
        QiblaUiState(
            userLocation = UserLocation(
                latitude = prayerPreferences.getLatitude(),
                longitude = prayerPreferences.getLongitude(),
                cityName = prayerPreferences.getCityName(),
                countryName = "",
                isGps = false
            ),
            calculationMethod = prayerPreferences.getMethod(),
            midnightMethod = prayerPreferences.getMidnightMethod(),
            minuteOffsets = prayerPreferences.getAllOffsets(),
            isGlobalNotificationEnabled = prayerPreferences.isGlobalNotificationEnabled,
            prayerNotificationMap = PrayerType.values().associateWith { prayerPreferences.isPrayerNotificationEnabled(it) },
            isNotificationPermissionGranted = PrayerNotificationHelper.canPostNotifications(application)
        )
    )
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var lastVibratedAligned = false
    private var lastVibrationTime = 0L

    init {
        // Create notification channel early
        PrayerNotificationHelper.createNotificationChannel(application)

        // Initial Qibla calculation
        val initialLat = _uiState.value.userLocation.latitude
        val initialLng = _uiState.value.userLocation.longitude
        updateQiblaBearing(initialLat, initialLng)

        // Observe sensor updates
        viewModelScope.launch {
            compassSensorManager.azimuth.collect { heading ->
                updateCompassHeading(heading)
            }
        }

        viewModelScope.launch {
            compassSensorManager.accuracy.collect { accuracy ->
                _uiState.update { it.copy(sensorAccuracy = accuracy) }
            }
        }

        viewModelScope.launch {
            compassSensorManager.isFlat.collect { flat ->
                _uiState.update { it.copy(isDeviceFlat = flat) }
            }
        }

        viewModelScope.launch {
            compassSensorManager.isSupported.collect { supported ->
                _uiState.update { it.copy(isSensorSupported = supported) }
            }
        }

        // Check permission and initial prayer calculation
        checkLocationPermissionAndRefresh()
        checkNotificationPermission()
        recalculatePrayers()
        startPeriodicCountdown()

        // Schedule WorkManager prayer alarms
        scheduleBackgroundPrayerNotifications()
    }

    fun onResume() {
        compassSensorManager.startListening()
        checkNotificationPermission()
    }

    fun onPause() {
        compassSensorManager.stopListening()
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(activeTab = index) }
    }

    fun checkLocationPermissionAndRefresh() {
        val hasPermission = locationHelper.hasLocationPermission()
        _uiState.update { it.copy(isLocationPermissionGranted = hasPermission) }
        if (hasPermission) {
            requestGpsLocation()
        }
    }

    fun checkNotificationPermission() {
        val hasPermission = PrayerNotificationHelper.canPostNotifications(getApplication())
        _uiState.update { it.copy(isNotificationPermissionGranted = hasPermission) }
    }

    fun requestGpsLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                prayerPreferences.saveLocation(location.latitude, location.longitude, location.cityName)
                _uiState.update {
                    it.copy(
                        userLocation = location,
                        selectedCityPreset = null,
                        isLoadingLocation = false
                    )
                }
                updateQiblaBearing(location.latitude, location.longitude)
                recalculatePrayers()
                scheduleBackgroundPrayerNotifications()
            } else {
                _uiState.update { it.copy(isLoadingLocation = false) }
            }
        }
    }

    fun selectCityPreset(city: CityPreset) {
        val newLocation = UserLocation(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.nameAr,
            countryName = city.nameEn,
            isGps = false
        )
        prayerPreferences.saveLocation(city.latitude, city.longitude, city.nameAr)
        _uiState.update {
            it.copy(
                userLocation = newLocation,
                selectedCityPreset = city
            )
        }
        updateQiblaBearing(city.latitude, city.longitude)
        recalculatePrayers()
        scheduleBackgroundPrayerNotifications()
    }

    fun setManualCoordinates(lat: Double, lng: Double, name: String = "موقع مخصص") {
        val newLocation = UserLocation(
            latitude = lat,
            longitude = lng,
            cityName = name,
            isGps = false
        )
        prayerPreferences.saveLocation(lat, lng, name)
        _uiState.update {
            it.copy(
                userLocation = newLocation,
                selectedCityPreset = null
            )
        }
        updateQiblaBearing(lat, lng)
        recalculatePrayers()
        scheduleBackgroundPrayerNotifications()
    }

    fun setCalculationMethod(method: ShiaCalculationMethod) {
        prayerPreferences.saveMethod(method)
        _uiState.update { it.copy(calculationMethod = method) }
        recalculatePrayers()
        scheduleBackgroundPrayerNotifications()
    }

    fun setMidnightMethod(method: MidnightMethod) {
        prayerPreferences.saveMidnightMethod(method)
        _uiState.update { it.copy(midnightMethod = method) }
        recalculatePrayers()
        scheduleBackgroundPrayerNotifications()
    }

    fun setSelectedDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        recalculatePrayers()
    }

    fun stepDate(days: Long) {
        val newDate = _uiState.value.selectedDate.plusDays(days)
        setSelectedDate(newDate)
    }

    fun setPrayerOffset(prayerType: PrayerType, offsetMinutes: Int) {
        prayerPreferences.saveOffset(prayerType, offsetMinutes)
        val current = _uiState.value.minuteOffsets.toMutableMap()
        current[prayerType] = offsetMinutes
        _uiState.update { it.copy(minuteOffsets = current) }
        recalculatePrayers()
        scheduleBackgroundPrayerNotifications()
    }

    fun toggleGlobalNotification(enabled: Boolean) {
        prayerPreferences.isGlobalNotificationEnabled = enabled
        _uiState.update { it.copy(isGlobalNotificationEnabled = enabled) }
        scheduleBackgroundPrayerNotifications()
    }

    fun togglePrayerNotification(type: PrayerType, enabled: Boolean) {
        prayerPreferences.setPrayerNotificationEnabled(type, enabled)
        val current = _uiState.value.prayerNotificationMap.toMutableMap()
        current[type] = enabled
        _uiState.update { it.copy(prayerNotificationMap = current) }
        scheduleBackgroundPrayerNotifications()
    }

    fun sendTestNotification() {
        PrayerNotificationHelper.showTestNotification(getApplication())
    }

    private fun scheduleBackgroundPrayerNotifications() {
        try {
            PrayerScheduler.scheduleAllPrayers(getApplication())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateQiblaBearing(lat: Double, lng: Double) {
        val bearing = QiblaCalculator.calculateQiblaBearing(lat, lng)
        val distance = QiblaCalculator.calculateDistanceToKaabaKm(lat, lng)
        _uiState.update {
            it.copy(
                qiblaBearing = bearing,
                distanceToKaabaKm = distance
            )
        }
        evaluateAlignment(_uiState.value.deviceHeading, bearing)
    }

    private fun updateCompassHeading(heading: Float) {
        _uiState.update { it.copy(deviceHeading = heading) }
        evaluateAlignment(heading, _uiState.value.qiblaBearing)
    }

    private fun evaluateAlignment(heading: Float, bearing: Float) {
        val offset = QiblaCalculator.getRelativeQiblaOffset(heading, bearing)
        val isAligned = QiblaCalculator.isPointingToQibla(heading, bearing, toleranceDegrees = 3.0f)

        _uiState.update {
            it.copy(
                qiblaOffsetAngle = offset,
                isAlignedWithQibla = isAligned
            )
        }

        // Trigger subtle haptic buzz when first locking onto Qibla
        if (isAligned && !lastVibratedAligned) {
            val now = System.currentTimeMillis()
            if (now - lastVibrationTime > 1500) { // Limit vibration to once per 1.5s
                vibrateAlignmentPulse()
                lastVibrationTime = now
            }
        }
        lastVibratedAligned = isAligned
    }

    private fun vibrateAlignmentPulse() {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(80)
                }
            }
        } catch (e: Exception) {
            // Safe fallback if vibration permission or hardware unavailable
        }
    }

    private fun recalculatePrayers() {
        val state = _uiState.value
        val schedule = ShiaPrayerCalculator.calculateDailySchedule(
            date = state.selectedDate,
            latitude = state.userLocation.latitude,
            longitude = state.userLocation.longitude,
            zoneId = ZoneId.systemDefault(),
            method = state.calculationMethod,
            midnightMethod = state.midnightMethod,
            minuteOffsets = state.minuteOffsets
        )
        _uiState.update { it.copy(prayerSchedule = schedule) }
    }

    private fun startPeriodicCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                // Refresh countdown timer every second
                val state = _uiState.value
                val schedule = state.prayerSchedule
                if (schedule != null && state.selectedDate == LocalDate.now()) {
                    recalculatePrayers()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compassSensorManager.stopListening()
        countdownJob?.cancel()
    }
}
