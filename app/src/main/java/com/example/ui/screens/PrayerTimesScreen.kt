package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import com.example.ui.theme.*
import com.example.viewmodel.QiblaUiState
import java.time.LocalDate
import java.util.Locale

@Composable
fun PrayerTimesScreen(
    uiState: QiblaUiState,
    onStepDate: (Long) -> Unit,
    onResetDateToToday: () -> Unit,
    onOpenCityPicker: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = uiState.prayerSchedule
    val isToday = uiState.selectedDate == LocalDate.now()
    var showFiqhInfo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Location & Method Bar
        item {
            LocationAndMethodHeader(
                locationName = uiState.userLocation.cityName,
                methodName = uiState.calculationMethod.arabicName,
                onOpenCityPicker = onOpenCityPicker,
                onNavigateToSettings = onNavigateToSettings
            )
        }

        // Date Switcher with Hijri and Gregorian
        item {
            DateNavigatorCard(
                hijriDate = schedule?.hijriDateString ?: "",
                gregorianDate = schedule?.gregorianDateString ?: "",
                isToday = isToday,
                onPrev = { onStepDate(-1) },
                onNext = { onStepDate(1) },
                onToday = onResetDateToToday
            )
        }

        // Next Prayer Hero Countdown Card (if available and viewing today)
        if (isToday && schedule?.nextPrayer != null) {
            item {
                NextPrayerHeroCard(
                    nextPrayer = schedule.nextPrayer,
                    remainingMillis = schedule.remainingMillis
                )
            }
        }

        // Fiqh Explanatory Expandable Banner
        item {
            FiqhGuidanceBanner(
                isExpanded = showFiqhInfo,
                onToggle = { showFiqhInfo = !showFiqhInfo }
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "جدول المواقيت اليومية وفق الفقه الإمامي",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = uiState.calculationMethod.arabicName.take(18) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicGold
                )
            }
        }

        // Prayer Times List
        if (schedule != null) {
            items(schedule.items, key = { it.type.name }) { prayerItem ->
                PrayerTimeRowItem(prayerItem = prayerItem)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LocationAndMethodHeader(
    locationName: String,
    methodName: String,
    onOpenCityPicker: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenCityPicker() }
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Location",
                    tint = IslamicMint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = methodName,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.testTag("prayer_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DateNavigatorCard(
    hijriDate: String,
    gregorianDate: String,
    isToday: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPrev,
                modifier = Modifier.testTag("prev_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day"
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToday() }
            ) {
                Text(
                    text = hijriDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold
                )
                Text(
                    text = gregorianDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!isToday) {
                    Text(
                        text = "اضغط للعودة إلى اليوم",
                        fontSize = 10.sp,
                        color = IslamicMint,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.testTag("next_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Day"
                )
            }
        }
    }
}

@Composable
private fun NextPrayerHeroCard(
    nextPrayer: PrayerTimeItem,
    remainingMillis: Long
) {
    val totalSeconds = (remainingMillis / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val formattedCountdown = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    Surface(
        color = IslamicEmeraldDark,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, IslamicMint.copy(alpha = 0.6f)),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("next_prayer_hero_card")
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            IslamicEmeraldMedium.copy(alpha = 0.6f),
                            IslamicEmeraldDark
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Next Prayer",
                        tint = IslamicMint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الصلاة القادمة",
                        style = MaterialTheme.typography.labelMedium,
                        color = IslamicMintSoft
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = nextPrayer.type.arabicName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = nextPrayer.formattedTime,
                    style = MaterialTheme.typography.titleLarge,
                    color = IslamicGoldLight,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = KaabaBlack.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicMint.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الوقت المتبقي: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = formattedCountdown,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicMint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = nextPrayer.type.descriptionAr,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FiqhGuidanceBanner(
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("fiqh_guidance_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Fiqh notes",
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مميزات المواقيت وفق الفقه الجعفري الإمامي",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    FiqhPointItem(
                        title = "وقت صلاة المغرب:",
                        description = "لا يدخل وقت المغرب بمجرد سقوط قرص الشمس تحت الأفق، بل يشترط زوال الحمرة المشرقية من جهة المشرق فوق الرأس (وهو ما يعادل هبوط الشمس 4 درجات تحت الأفق - معهد لواء بقم المقدسة ومكتب السيد السيستاني)."
                    )
                    FiqhPointItem(
                        title = "منتصف الليل الشرعي:",
                        description = "ينتصف الليل شرعاً في الفقه الشيعي عند منتصف المدة بين المغرب والفجر (أو الغروب والفجر). وهو الغاية لنهاية وقت أداء صلاتي المغرب والعشاء للمختار قبل أن تصير قضاءً."
                    )
                    FiqhPointItem(
                        title = "صلاة الصبح (الفجر):",
                        description = "محسوبة عند طلوع الفجر الصادق بزاوية انخفاض 16 درجة، وهي المعتمدة في حوزتي النجف وقم."
                    )
                    FiqhPointItem(
                        title = "الجمع والفضيلة:",
                        description = "يجوز الجمع بين الظهرين (الظهر والعصر) وبين العشاءين (المغرب والعشاء)، مع استحباب أداء كل صلاة في وقت فضيلتها."
                    )
                }
            }
        }
    }
}

@Composable
private fun FiqhPointItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldDark
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun PrayerTimeRowItem(prayerItem: PrayerTimeItem) {
    val isNext = prayerItem.isNext
    val isCurrent = prayerItem.isCurrentWindow
    val type = prayerItem.type

    val icon = when (type) {
        PrayerType.IMSAK -> Icons.Default.NightsStay
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.SUNRISE -> Icons.Default.WbSunny
        PrayerType.DHUHR -> Icons.Default.LightMode
        PrayerType.ASR -> Icons.Default.Brightness5
        PrayerType.SUNSET -> Icons.Default.WbTwilight
        PrayerType.MAGHRIB -> Icons.Default.DarkMode
        PrayerType.ISHA -> Icons.Default.Bedtime
        PrayerType.MIDNIGHT -> Icons.Default.Nightlight
    }

    val iconColor = when {
        isNext -> IslamicMint
        type == PrayerType.MAGHRIB -> IslamicGold
        type.isObligatory -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = when {
            isNext -> IslamicEmerald.copy(alpha = 0.25f)
            isCurrent -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isNext) 1.5.dp else 1.dp,
            color = if (isNext) IslamicMint else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        tonalElevation = if (isNext) 3.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_row_${type.name}")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = iconColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = type.arabicName,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = type.arabicName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isNext || type.isObligatory) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNext) IslamicMintSoft else MaterialTheme.colorScheme.onSurface
                        )
                        if (isNext) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = IslamicMint.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "القادمة",
                                    color = IslamicMint,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        } else if (type == PrayerType.MAGHRIB) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = IslamicGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "زوال الحمرة",
                                    color = IslamicGoldDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = type.descriptionAr,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = prayerItem.formattedTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isNext) IslamicMint else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
