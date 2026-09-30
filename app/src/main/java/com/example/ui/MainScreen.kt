package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.CityPreset
import com.example.ui.screens.CitySelectionDialog
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.QiblaCompassScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicMint
import com.example.viewmodel.QiblaPrayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: QiblaPrayerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCityPickerFromHeader by remember { mutableStateOf(false) }

    // Runtime Permission Launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.checkLocationPermissionAndRefresh()
        }
    }

    // Runtime Permission Launcher for Push Notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.checkNotificationPermission()
    }

    LaunchedEffect(Unit) {
        if (!uiState.isLocationPermissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !uiState.isNotificationPermissionGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "قبلتي ومواقيت الصلاة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.prayerSchedule?.hijriDateString?.let { hijri ->
                            Text(
                                text = hijri,
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicGold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCityPickerFromHeader = true },
                        modifier = Modifier.testTag("top_bar_city_picker")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "Holy Sanctuaries & Cities",
                            tint = IslamicGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == 0) Icons.Default.Explore else Icons.Outlined.Explore,
                            contentDescription = "Qibla"
                        )
                    },
                    label = { Text("القبلة") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IslamicMint,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_qibla")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == 1) Icons.Default.AccessTimeFilled else Icons.Outlined.AccessTime,
                            contentDescription = "Prayer Times"
                        )
                    },
                    label = { Text("المواقيت") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IslamicMint,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_prayers")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeTab == 2) Icons.Default.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("الإعدادات") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IslamicMint,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                0 -> {
                    QiblaCompassScreen(
                        uiState = uiState,
                        onRequestLocationPermission = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        onRefreshGps = {
                            if (!uiState.isLocationPermissionGranted) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                viewModel.requestGpsLocation()
                            }
                        },
                        onOpenCityPicker = { showCityPickerFromHeader = true }
                    )
                }
                1 -> {
                    PrayerTimesScreen(
                        uiState = uiState,
                        onStepDate = { step -> viewModel.stepDate(step) },
                        onResetDateToToday = { viewModel.setSelectedDate(java.time.LocalDate.now()) },
                        onOpenCityPicker = { showCityPickerFromHeader = true },
                        onNavigateToSettings = { viewModel.setTab(2) }
                    )
                }
                2 -> {
                    SettingsScreen(
                        uiState = uiState,
                        onRefreshGps = {
                            if (!uiState.isLocationPermissionGranted) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                viewModel.requestGpsLocation()
                            }
                        },
                        onSelectCity = { city -> viewModel.selectCityPreset(city) },
                        onSetManualCoordinates = { lat, lng, name -> viewModel.setManualCoordinates(lat, lng, name) },
                        onSetMethod = { method -> viewModel.setCalculationMethod(method) },
                        onSetMidnightMethod = { mMethod -> viewModel.setMidnightMethod(mMethod) },
                        onSetOffset = { pType, offset -> viewModel.setPrayerOffset(pType, offset) },
                        onToggleGlobalNotification = { enabled -> viewModel.toggleGlobalNotification(enabled) },
                        onTogglePrayerNotification = { pType, enabled -> viewModel.togglePrayerNotification(pType, enabled) },
                        onSendTestNotification = { viewModel.sendTestNotification() },
                        onRequestNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCityPickerFromHeader) {
        CitySelectionDialog(
            onDismiss = { showCityPickerFromHeader = false },
            onSelect = { city ->
                viewModel.selectCityPreset(city)
                showCityPickerFromHeader = false
            }
        )
    }
}
