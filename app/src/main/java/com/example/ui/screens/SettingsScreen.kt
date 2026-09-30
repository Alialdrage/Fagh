package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.QiblaUiState
import java.util.Locale

@Composable
fun SettingsScreen(
    uiState: QiblaUiState,
    onRefreshGps: () -> Unit,
    onSelectCity: (CityPreset) -> Unit,
    onSetManualCoordinates: (Double, Double, String) -> Unit,
    onSetMethod: (ShiaCalculationMethod) -> Unit,
    onSetMidnightMethod: (MidnightMethod) -> Unit,
    onSetOffset: (PrayerType, Int) -> Unit,
    onToggleGlobalNotification: (Boolean) -> Unit,
    onTogglePrayerNotification: (PrayerType, Boolean) -> Unit,
    onSendTestNotification: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    var showCoordDialog by remember { mutableStateOf(false) }
    var testNotifSent by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notification & Background Service Section (WorkManager)
        item {
            SettingsSectionHeader(title = "تنبيهات وإشعارات أوقات الصلاة (WorkManager)", icon = Icons.Default.NotificationsActive)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().testTag("notifications_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Notification permission banner if not granted
                    if (!uiState.isNotificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "إذن الإشعارات غير مفعّل",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "يرجى منح الإذن لتصلك تنبيهات دخول أوقات الصلاة في الخلفية.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                TextButton(
                                    onClick = onRequestNotificationPermission,
                                    modifier = Modifier.testTag("request_notification_permission_button")
                                ) {
                                    Text("تفعيل", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Global notification switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تفعيل إشعارات الصلوات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "إرسال إشعار محلي مع صوت واهتزاز عند حلول كل صلاة عبر خدمة مجدولة في الخلفية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isGlobalNotificationEnabled,
                            onCheckedChange = { onToggleGlobalNotification(it) },
                            modifier = Modifier.testTag("global_notification_switch")
                        )
                    }

                    AnimatedVisibility(visible = uiState.isGlobalNotificationEnabled) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "تحديد الصلوات المراد التنبيه إليها:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            PrayerType.values().forEach { pType ->
                                val isEnabled = uiState.prayerNotificationMap[pType] ?: pType.isObligatory
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onTogglePrayerNotification(pType, !isEnabled) }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (pType.isObligatory) Icons.Default.Mosque else Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = if (isEnabled) IslamicMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = pType.arabicName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (pType == PrayerType.MAGHRIB) {
                                                Text(
                                                    text = "عند زوال الحمرة المشرقية (الفقه الجعفري)",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontSize = 10.sp,
                                                    color = IslamicGold
                                                )
                                            }
                                        }
                                    }
                                    Checkbox(
                                        checked = isEnabled,
                                        onCheckedChange = { onTogglePrayerNotification(pType, it) },
                                        modifier = Modifier.testTag("checkbox_notif_${pType.name}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Test Notification Button
                            Button(
                                onClick = {
                                    onSendTestNotification()
                                    testNotifSent = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("send_test_notification_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (testNotifSent) "تم إرسال الإشعار التجريبي ✓" else "إرسال إشعار تجريبي الآن للتأكد",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Location Settings Section
        item {
            SettingsSectionHeader(title = "الموقع الجغرافي والإحداثيات", icon = Icons.Default.Place)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الموقع الحالي المعتمد:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.userLocation.cityName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "خط العرض: %.4f° | خط الطول: %.4f°",
                                    uiState.userLocation.latitude,
                                    uiState.userLocation.longitude
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicGoldDark
                            )
                        }

                        if (uiState.userLocation.isGps) {
                            Surface(
                                color = IslamicMint.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "GPS مفعّل",
                                    color = IslamicEmeraldMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onRefreshGps,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("use_gps_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تحديث عبر GPS", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showCityDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_city_button")
                        ) {
                            Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختر مدينة", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showCoordDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_coords_button")
                    ) {
                        Icon(Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إدخال إحداثيات يدوياً")
                    }
                }
            }
        }

        // Shia Calculation Method Section
        item {
            SettingsSectionHeader(title = "طريقة الحساب الفقهي الشيعي", icon = Icons.Default.MenuBook)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ShiaCalculationMethod.values().forEach { method ->
                        val isSelected = uiState.calculationMethod == method
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetMethod(method) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSetMethod(method) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = method.arabicName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = method.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Midnight Method Section
        item {
            SettingsSectionHeader(title = "طريقة احتساب منتصف الليل الشرعي", icon = Icons.Default.NightsStay)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MidnightMethod.values().forEach { mMethod ->
                        val isSelected = uiState.midnightMethod == mMethod
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetMidnightMethod(mMethod) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSetMidnightMethod(mMethod) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mMethod.arabicName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Minute Adjustments Section
        item {
            SettingsSectionHeader(title = "تعديل الدقائق يدوياً (احتياط محلي)", icon = Icons.Default.Tune)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "يمكنك إضافة أو خصم دقائق لكل صلاة بما يتناسب مع التقويم المعتمد في مدينتك:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val editablePrayers = listOf(
                        PrayerType.FAJR,
                        PrayerType.DHUHR,
                        PrayerType.ASR,
                        PrayerType.MAGHRIB,
                        PrayerType.ISHA
                    )

                    editablePrayers.forEach { pType ->
                        val offset = uiState.minuteOffsets[pType] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pType.arabicName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onSetOffset(pType, offset - 1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    text = if (offset > 0) "+$offset د" else "$offset د",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (offset != 0) IslamicGoldDark else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.width(44.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                IconButton(
                                    onClick = { onSetOffset(pType, offset + 1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Compass Sensor Calibration Info
        item {
            SettingsSectionHeader(title = "معايرة مستشعر البوصلة", icon = Icons.Default.Sensors)
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Calibration",
                        tint = IslamicGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "كيف تتم معايرة البوصلة؟",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "للتخلص من تأثير المجالات المغناطيسية المحيطة، أمسك الجهاز وأدره في الهواء برسم الرقم (8) بالإنجليزي عدة مرات حتى ترتفع دقة المستشعر إلى 'عالية'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        CitySelectionDialog(
            onDismiss = { showCityDialog = false },
            onSelect = { city ->
                onSelectCity(city)
                showCityDialog = false
            }
        )
    }

    // Manual Coordinates Dialog
    if (showCoordDialog) {
        ManualCoordinatesDialog(
            currentLat = uiState.userLocation.latitude,
            currentLng = uiState.userLocation.longitude,
            onDismiss = { showCoordDialog = false },
            onSave = { lat, lng, name ->
                onSetManualCoordinates(lat, lng, name)
                showCoordDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IslamicMint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CitySelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (CityPreset) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "اختر العتبة المقدسة أو المدينة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                items(HolyCitiesPresets.CITIES) { city ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(city) }
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (city.isHolySanctuary) Icons.Default.Mosque else Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (city.isHolySanctuary) IslamicGold else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = city.nameAr,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = city.nameEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun ManualCoordinatesDialog(
    currentLat: Double,
    currentLng: Double,
    onDismiss: () -> Unit,
    onSave: (Double, Double, String) -> Unit
) {
    var latText by remember { mutableStateOf(currentLat.toString()) }
    var lngText by remember { mutableStateOf(currentLng.toString()) }
    var nameText by remember { mutableStateOf("موقع مخصص") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إدخال الإحداثيات الجغرافية") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("اسم الموقع") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = latText,
                    onValueChange = { latText = it },
                    label = { Text("خط العرض (Latitude: -90 إلى 90)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lngText,
                    onValueChange = { lngText = it },
                    label = { Text("خط الطول (Longitude: -180 إلى 180)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (hasError) {
                    Text(
                        text = "يرجى إدخال قيم عددية صحيحة لخط العرض والطول",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latText.toDoubleOrNull()
                    val lng = lngText.toDoubleOrNull()
                    if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) {
                        onSave(lat, lng, nameText.ifBlank { "موقع مخصص" })
                    } else {
                        hasError = true
                    }
                }
            ) {
                Text("حفظ وتطبيق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
