package com.example.ui.hilal

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HilalDatabase
import com.example.model.HilalCityData
import com.example.model.HilalFiqhRule
import com.example.model.HilalMonthRecord
import com.example.model.HilalVisibilityStatus
import com.example.model.IslamicEvent
import com.example.util.TtsManager

@Composable
fun HilalTimingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ttsManager = remember { TtsManager(context) }
    val isSpeaking by ttsManager.isSpeaking.collectAsState()
    val currentUtterance by ttsManager.currentUtteranceId.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val months = remember { HilalDatabase.monthsData }
    val fiqhRules = remember { HilalDatabase.fiqhHilalRules }

    var selectedMonthIndex by remember { mutableIntStateOf(8) } // Default to Month 9: Ramadan (index 8)
    var selectedCityName by remember { mutableStateOf("النجف الأشرف") }
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: جدول الشهور، 1: الضوابط الفقهية

    val currentMonth = months.getOrElse(selectedMonthIndex) { months.first() }

    // City data for selected month
    val currentCityData: HilalCityData = remember(currentMonth, selectedCityName) {
        if (selectedCityName == currentMonth.primaryCity.cityName) {
            currentMonth.primaryCity
        } else {
            currentMonth.otherCities.find { it.cityName == selectedCityName } ?: currentMonth.primaryCity
        }
    }

    val availableCities = remember(currentMonth) {
        listOf(currentMonth.primaryCity.cityName) + currentMonth.otherCities.map { it.cityName }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hilal_timings_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // Hero Header Banner
        item {
            HilalHeaderBanner()
        }

        // Segmented Tabs: مواقيت الأهلة 1446 / الأحكام والضوابط الفقهية
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("جدول مواقيت الأهلة (1446هـ)", "الضوابط الفقهية لثبوت الهلال")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTopTab == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTopTab = index }
                                .testTag("hilal_tab_$index"),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (selectedTopTab == 0) {
            // Month Selector Carousel
            item {
                Text(
                    text = "اختر الشهر الهجري:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hilal_month_selector"),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(months.indices.toList()) { index ->
                        val month = months[index]
                        val isSelected = selectedMonthIndex == index
                        MonthChipItem(
                            monthRecord = month,
                            isSelected = isSelected,
                            onClick = {
                                selectedMonthIndex = index
                                // If speaking another month, stop speech
                                if (isSpeaking) ttsManager.stop()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // City / Horizon Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "أفق الرصد والموقع:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableCities) { city ->
                        val isSelected = selectedCityName == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCityName = city },
                            label = {
                                Text(
                                    text = if (city == "النجف الأشرف") "$city (المعتمد)" else city,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Main Featured Crescent Card
            item {
                FeaturedCrescentCard(
                    monthRecord = currentMonth,
                    cityData = currentCityData,
                    isSpeaking = isSpeaking,
                    onToggleTts = {
                        if (isSpeaking) {
                            ttsManager.stop()
                        } else {
                            val speechText = buildHilalSpeechText(currentMonth, currentCityData)
                            ttsManager.speak(speechText, "hilal_${currentMonth.id}")
                        }
                    },
                    onShare = {
                        shareHilalData(context, currentMonth, currentCityData)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Astronomical Sighting Parameters Grid
            item {
                AstronomicalDetailsGrid(
                    monthRecord = currentMonth,
                    cityData = currentCityData,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Religious Occasions Section for this month
            if (currentMonth.events.isNotEmpty()) {
                item {
                    ReligiousOccasionsSection(
                        monthName = currentMonth.monthNameArabic,
                        events = currentMonth.events,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Quick Sistani Fatwa note banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تنبيه فقهي معتمد من مكتب سماحة السيد",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "هذه التوقعات الفلكية تُعد إرشادية وتعين على رصد الهلال، ولا يثبت دخول الشهر الشرعي شرعاً عند سماحة السيد إلا بالرؤية الحسية بالعين المجردة أو إتمام العدة ثلاثين يوماً، ولا عبرة بالرؤية بالتلسكوبات وحدها.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Tab 1: Fiqh Rules on Hilal Sighting
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "الضوابط الفقهية لرؤية وثبوت الهلال",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "وفق فتاوى سماحة آية الله العظمى السيد علي السيستاني (دام ظله) في منهاج الصالحين والمسائل المنتخبة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    fiqhRules.forEach { rule ->
                        FiqhRuleExpandableCard(rule = rule)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HilalHeaderBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مواقيت الأهلة والشهور الهجرية",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "كراس مواقيت الأهلة الرسمي - مكتب سماحة السيد السيستاني (دام ظله) بالنجف الأشرف لعام 1446هـ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }

                // Decorative Crescent Icon
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(56.dp)
                        .padding(start = 8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Brightness2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthChipItem(
    monthRecord: HilalMonthRecord,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val statusColor = Color(monthRecord.primaryCity.visibilityStatus.colorHex)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = Modifier.testTag("month_chip_${monthRecord.monthIndex}")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${monthRecord.monthIndex}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = monthRecord.monthNameArabic,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FeaturedCrescentCard(
    monthRecord: HilalMonthRecord,
    cityData: HilalCityData,
    isSpeaking: Boolean,
    onToggleTts: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = cityData.visibilityStatus
    val statusColor = Color(status.colorHex)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("featured_crescent_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Month Name + Audio / Share Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = monthRecord.monthTitleFull,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "أفق: ${cityData.cityName} - تحري مساء: ${monthRecord.observationEvening}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                Row {
                    // TTS Audio Button
                    Surface(
                        shape = CircleShape,
                        color = if (isSpeaking) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("hilal_tts_button")
                    ) {
                        IconButton(onClick = onToggleTts) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "إيقاف القراءة" else "قراءة تقرير الهلال صوتياً",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Share Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("hilal_share_button")
                    ) {
                        IconButton(onClick = onShare) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "مشاركة تقرير الهلال",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Crescent Illustration & Visibility Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Moon Crescent graphic representation
                    CrescentMoonVisual(
                        illuminationPercent = cityData.illuminationPercent,
                        modifier = Modifier.size(68.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Badge of status
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = status.titleArabic,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = status.descriptionArabic,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expected First Day Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "اليوم الأول المتوقع للشهر شرعاً:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = monthRecord.expectedFirstDay,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official Statement of Ayatollah Sistani's Office
            Text(
                text = "بيان قسم رصد الأهلة في مكتب سماحة السيد:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = monthRecord.sistaniFiqhStatement,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Fiqh note regarding this status
            Text(
                text = "الحكم الفقهي: ${status.sistaniRulingNote}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun CrescentMoonVisual(
    illuminationPercent: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer dark night sky circle
            drawCircle(
                color = Color(0xFF1A233A),
                radius = radius,
                center = center
            )

            // Golden crescent illumination arc
            val crescentColor = Color(0xFFFFD54F)
            val path = Path()

            // Draw crescent using arc and cubic curves
            val crescentThickness = (radius * (illuminationPercent / 3.0).coerceIn(0.18, 0.65)).toFloat()

            // Outer circle
            path.moveTo(center.x, center.y - radius * 0.85f)
            path.cubicTo(
                center.x + radius * 0.95f, center.y - radius * 0.7f,
                center.x + radius * 0.95f, center.y + radius * 0.7f,
                center.x, center.y + radius * 0.85f
            )

            // Inner crescent curve
            path.cubicTo(
                center.x + radius * 0.95f - crescentThickness, center.y + radius * 0.5f,
                center.x + radius * 0.95f - crescentThickness, center.y - radius * 0.5f,
                center.x, center.y - radius * 0.85f
            )
            path.close()

            drawPath(path = path, color = crescentColor, style = Fill)

            // Small twinkling stars in background
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 1.5.dp.toPx(), center = Offset(center.x - radius * 0.4f, center.y - radius * 0.3f))
            drawCircle(Color.White.copy(alpha = 0.7f), radius = 1.0.dp.toPx(), center = Offset(center.x - radius * 0.2f, center.y + radius * 0.4f))
            drawCircle(Color.White.copy(alpha = 0.6f), radius = 1.2.dp.toPx(), center = Offset(center.x - radius * 0.5f, center.y + radius * 0.1f))
        }
    }
}

@Composable
fun AstronomicalDetailsGrid(
    monthRecord: HilalMonthRecord,
    cityData: HilalCityData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الإحداثيات والبيانات الفلكية لحظة الغروب",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: مكث الهلال بعد الغروب + ارتفاع الهلال
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ParamInfoBox(
                    label = "مدة المكث بعد الغروب",
                    value = "${cityData.stayDurationMinutes} دقيقة",
                    icon = Icons.Default.Timer,
                    modifier = Modifier.weight(1f)
                )
                ParamInfoBox(
                    label = "ارتفاع الهلال عن الأفق",
                    value = "${cityData.altitudeDegrees}° درجة",
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: نسبة القسم المنار + البعد الزاوي
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ParamInfoBox(
                    label = "نسبة القسم المنار (الإضاءة)",
                    value = "${cityData.illuminationPercent}%",
                    icon = Icons.Default.Brightness2,
                    modifier = Modifier.weight(1f)
                )
                ParamInfoBox(
                    label = "عمر الهلال الفلكي",
                    value = "${monthRecord.moonAgeHours} ساعة",
                    icon = Icons.Default.Event,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: أوقات الغروب
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ParamInfoBox(
                    label = "غروب الشمس",
                    value = cityData.sunsetTime,
                    icon = Icons.Default.WbSunny,
                    modifier = Modifier.weight(1f)
                )
                ParamInfoBox(
                    label = "غروب القمر",
                    value = cityData.moonsetTime,
                    icon = Icons.Default.NightsStay,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Conjunction row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "الاقتران المركزي (ولادة الهلال الفلكية):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = monthRecord.conjunctionDateTime,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ParamInfoBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ReligiousOccasionsSection(
    monthName: String,
    events: List<IslamicEvent>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "المناسبات والذكريات الإسلامية في $monthName",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            events.forEachIndexed { index, event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (event.isMajor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${event.day}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (event.isMajor) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (event.isMajor) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                if (index < events.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun FiqhRuleExpandableCard(rule: HilalFiqhRule) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = rule.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "طي" else "توسيع",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rule.questionOrTopic,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 20.sp
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = rule.ruling,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "المصدر: ${rule.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun buildHilalSpeechText(month: HilalMonthRecord, city: HilalCityData): String {
    return """
        تقرير هلال ${month.monthTitleFull} الصادر عن مكتب سماحة السيد السيستاني.
        أفق مدينة ${city.cityName}.
        يوم التحري والاستهلال: ${month.observationEvening}.
        حالة الرؤية المتوقعة: ${city.visibilityStatus.titleArabic}.
        مدة مكث الهلال بعد غروب الشمس: ${city.stayDurationMinutes} دقيقة.
        ارتفاع الهلال: ${city.altitudeDegrees} درجة.
        نسبة الإضاءة: ${city.illuminationPercent} بالمئة.
        غروب الشمس الساعة ${city.sunsetTime}، وغروب القمر الساعة ${city.moonsetTime}.
        اليوم الأول المتوقع للشهر شرعاً: ${month.expectedFirstDay}.
        ${month.sistaniFiqhStatement}.
    """.trimIndent()
}

private fun shareHilalData(context: Context, month: HilalMonthRecord, city: HilalCityData) {
    val shareContent = """
🌙 ${month.monthTitleFull}
📋 صادر طبقاً لبيانات قسم رصد الأهلة في مكتب سماحة آية الله العظمى السيد السيستاني (دام ظله)

📍 الأفق: ${city.cityName}
📅 مساء التحري: ${month.observationEvening}
🔭 إمكانية الرؤية: ${city.visibilityStatus.titleArabic}
⏳ مدة المكث بعد الغروب: ${city.stayDurationMinutes} دقيقة
📐 الارتفاع عن الأفق: ${city.altitudeDegrees}°
✨ نسبة القسم المنار: ${city.illuminationPercent}%
🌅 غروب الشمس: ${city.sunsetTime} | 🌙 غروب القمر: ${city.moonsetTime}
📆 اليوم الأول المتوقع للشهر شرعاً: ${month.expectedFirstDay}

📜 البيان الشرعي:
${month.sistaniFiqhStatement}

📌 ${city.visibilityStatus.sistaniRulingNote}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, month.monthTitleFull)
        putExtra(Intent.EXTRA_TEXT, shareContent)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة تقرير الهلال"))
}
