package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.QiblaCalculator
import com.example.model.SensorAccuracyLevel
import com.example.ui.theme.*
import com.example.viewmodel.QiblaUiState
import kotlin.math.*

@Composable
fun QiblaCompassScreen(
    uiState: QiblaUiState,
    onRequestLocationPermission: () -> Unit,
    onRefreshGps: () -> Unit,
    onOpenCityPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Smooth compass rotation animation
    val animatedHeading by animateFloatAsState(
        targetValue = uiState.deviceHeading,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "compassRotation"
    )

    val isAligned = uiState.isAlignedWithQibla

    // Glowing aura color for Qibla alignment
    val auraGlowColor by animateColorAsState(
        targetValue = if (isAligned) IslamicMint.copy(alpha = 0.45f) else Color.Transparent,
        animationSpec = tween(400),
        label = "glowColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isAligned) 1.06f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location & GPS status header card
        LocationHeaderBar(
            locationName = uiState.userLocation.cityName,
            isGps = uiState.userLocation.isGps,
            isLoading = uiState.isLoadingLocation,
            onRefreshGps = onRefreshGps,
            onOpenCityPicker = onOpenCityPicker
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Device flatness / level tip if tilted
        if (!uiState.isDeviceFlat) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicAmber.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "Tilt warning",
                        tint = IslamicAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "يرجى وضع الجهاز بشكل أفقي مستوٍ للحصول على أعلى دقة للبوصلة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Qibla Alignment Banner
        AlignmentStatusBadge(
            isAligned = isAligned,
            offsetAngle = uiState.qiblaOffsetAngle,
            qiblaBearing = uiState.qiblaBearing
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Circular Interactive Compass Dial
        Box(
            modifier = Modifier
                .size(310.dp)
                .testTag("qibla_compass_dial"),
            contentAlignment = Alignment.Center
        ) {
            // Background pulsing glow when aligned
            if (isAligned) {
                Box(
                    modifier = Modifier
                        .size(310.dp * pulseScale)
                        .clip(CircleShape)
                        .background(auraGlowColor)
                )
            }

            // Outer decorative ring
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        width = if (isAligned) 3.dp else 1.5.dp,
                        color = if (isAligned) IslamicMint else IslamicGold.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )

            // Rotating Compass Rose & Ticks Canvas
            Canvas(
                modifier = Modifier
                    .size(270.dp)
                    .rotate(-animatedHeading)
            ) {
                drawCompassRose(
                    center = center,
                    radius = size.minDimension / 2f,
                    qiblaBearing = uiState.qiblaBearing,
                    isAligned = isAligned
                )
            }

            // Fixed Top Reference Index (Phone's forward direction)
            Canvas(modifier = Modifier.size(290.dp)) {
                val cx = size.width / 2f
                val arrowPath = Path().apply {
                    moveTo(cx, 4.dp.toPx())
                    lineTo(cx - 9.dp.toPx(), 20.dp.toPx())
                    lineTo(cx, 16.dp.toPx())
                    lineTo(cx + 9.dp.toPx(), 20.dp.toPx())
                    close()
                }
                drawPath(
                    path = arrowPath,
                    color = if (isAligned) IslamicMint else IslamicGoldDark
                )
            }

            // Center Kaaba Emblem & Status
            KaabaCenterEmblem(
                isAligned = isAligned,
                deviceHeading = animatedHeading,
                qiblaBearing = uiState.qiblaBearing
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Key Information Metrics (Bearing, Heading, Distance, Accuracy)
        CompassMetricsCards(
            qiblaBearing = uiState.qiblaBearing,
            deviceHeading = uiState.deviceHeading,
            distanceKm = uiState.distanceToKaabaKm,
            sensorAccuracy = uiState.sensorAccuracy
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calibration tip
        if (uiState.sensorAccuracy == SensorAccuracyLevel.LOW || uiState.sensorAccuracy == SensorAccuracyLevel.UNRELIABLE) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Calibration figure 8",
                        tint = IslamicGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "لمعايرة مستشعر البوصلة، حرك الهاتف في الهواء بحركة تشبه رقم 8 بالإنجليزية (∞)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LocationHeaderBar(
    locationName: String,
    isGps: Boolean,
    isLoading: Boolean,
    onRefreshGps: () -> Unit,
    onOpenCityPicker: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isGps) IslamicMint.copy(alpha = 0.2f) else IslamicGold.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isGps) Icons.Default.MyLocation else Icons.Default.LocationCity,
                            contentDescription = "Location source",
                            tint = if (isGps) IslamicMint else IslamicGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = locationName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isGps) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = IslamicMint.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "GPS",
                                    color = IslamicEmeraldMedium,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isGps) "موقع تم تحديده عبر الأقمار الاصطناعية" else "مدينة مختارة يدوياً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onRefreshGps,
                    enabled = !isLoading,
                    modifier = Modifier.testTag("refresh_gps_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh GPS",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(
                    onClick = onOpenCityPicker,
                    modifier = Modifier.testTag("open_city_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Choose Sanctuary or City",
                        tint = IslamicGold
                    )
                }
            }
        }
    }
}

@Composable
private fun AlignmentStatusBadge(
    isAligned: Boolean,
    offsetAngle: Float,
    qiblaBearing: Float
) {
    val roundedBearing = qiblaBearing.roundToInt()
    val roundedOffset = abs(offsetAngle.roundToInt())

    Surface(
        color = if (isAligned) IslamicMint.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isAligned) 1.5.dp else 1.dp,
            color = if (isAligned) IslamicMint else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alignment_status_card")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Explore,
                contentDescription = "Status",
                tint = if (isAligned) IslamicMint else IslamicGold,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                if (isAligned) {
                    Text(
                        text = "أنت باتجاه القبلة الشريفة الآن!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) IslamicEmeraldMedium else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "زاوية الكعبة المشرفة: $roundedBearing°",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val turnDirection = if (offsetAngle > 0) "إلى اليمين ↻" else "إلى اليسار ↺"
                    Text(
                        text = "أدر الهاتف $roundedOffset° $turnDirection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اتجاه القبلة المطلوب: $roundedBearing° من الشمال",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun KaabaCenterEmblem(
    isAligned: Boolean,
    deviceHeading: Float,
    qiblaBearing: Float
) {
    Surface(
        shape = CircleShape,
        color = if (isAligned) IslamicEmeraldDark else KaabaBlack,
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (isAligned) IslamicMint else IslamicGold
        ),
        modifier = Modifier
            .size(96.dp)
            .shadow(8.dp, CircleShape)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Kaaba Cube representation
            Box(
                modifier = Modifier
                    .size(36.dp, 32.dp)
                    .background(Color(0xFF18181B), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(4.dp))
            ) {
                // Gold belt (Hizam)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(top = 5.dp)
                        .background(IslamicGold)
                )
                // Gold Door
                Box(
                    modifier = Modifier
                        .size(6.dp, 12.dp)
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 2.dp)
                        .background(IslamicGoldLight)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${deviceHeading.roundToInt()}°",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isAligned) IslamicMintSoft else IslamicGoldLight
            )
        }
    }
}

private fun DrawScope.drawCompassRose(
    center: Offset,
    radius: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    // Draw tick marks around circumference
    for (i in 0 until 360 step 5) {
        val angleRad = Math.toRadians(i.toDouble() - 90.0)
        val isMajor = i % 30 == 0
        val isCardinal = i % 90 == 0

        val tickLength = when {
            isCardinal -> 16.dp.toPx()
            isMajor -> 10.dp.toPx()
            else -> 5.dp.toPx()
        }

        val strokeW = when {
            isCardinal -> 2.5f.dp.toPx()
            isMajor -> 1.5f.dp.toPx()
            else -> 1f.dp.toPx()
        }

        val tickColor = when {
            i == 0 -> Color(0xFFEF4444) // North in Red
            isCardinal -> Color.White.copy(alpha = 0.9f)
            isMajor -> IslamicGold.copy(alpha = 0.7f)
            else -> Color.Gray.copy(alpha = 0.4f)
        }

        val startX = center.x + (radius - tickLength) * cos(angleRad).toFloat()
        val startY = center.y + (radius - tickLength) * sin(angleRad).toFloat()
        val endX = center.x + radius * cos(angleRad).toFloat()
        val endY = center.y + radius * sin(angleRad).toFloat()

        drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }

    // Draw Qibla Pointer Indicator on the rim
    val qiblaAngleRad = Math.toRadians(qiblaBearing.toDouble() - 90.0)
    val qiblaMarkerRadius = radius - 8.dp.toPx()
    val qiblaX = center.x + qiblaMarkerRadius * cos(qiblaAngleRad).toFloat()
    val qiblaY = center.y + qiblaMarkerRadius * sin(qiblaAngleRad).toFloat()

    // Line from center to Qibla
    drawLine(
        color = if (isAligned) IslamicMint.copy(alpha = 0.8f) else IslamicGold.copy(alpha = 0.5f),
        start = center,
        end = Offset(qiblaX, qiblaY),
        strokeWidth = if (isAligned) 3.dp.toPx() else 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Kaaba Marker circle on the rim
    drawCircle(
        color = if (isAligned) IslamicMint else IslamicGold,
        radius = 11.dp.toPx(),
        center = Offset(qiblaX, qiblaY)
    )
    drawCircle(
        color = KaabaBlack,
        radius = 8.dp.toPx(),
        center = Offset(qiblaX, qiblaY)
    )
    drawCircle(
        color = if (isAligned) IslamicMint else IslamicGoldLight,
        radius = 3.5.dp.toPx(),
        center = Offset(qiblaX, qiblaY)
    )

    // Draw North Arrow
    val northRad = Math.toRadians(-90.0)
    val northX = center.x + (radius - 24.dp.toPx()) * cos(northRad).toFloat()
    val northY = center.y + (radius - 24.dp.toPx()) * sin(northRad).toFloat()
    drawCircle(
        color = Color(0xFFEF4444),
        radius = 4.dp.toPx(),
        center = Offset(northX, northY)
    )
}

@Composable
private fun CompassMetricsCards(
    qiblaBearing: Float,
    deviceHeading: Float,
    distanceKm: Double,
    sensorAccuracy: SensorAccuracyLevel
) {
    val cardinalText = QiblaCalculator.getCompassDirectionText(qiblaBearing)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "زاوية القبلة",
                value = "${qiblaBearing.roundToInt()}°",
                subtitle = "${cardinalText.first} (${cardinalText.second})",
                icon = Icons.Default.NearMe,
                tint = IslamicGold,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "المسافة إلى مكة",
                value = "${distanceKm.roundToInt()} كم",
                subtitle = "خط مستقيم",
                icon = Icons.Default.Straighten,
                tint = IslamicMint,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "اتجاه الجهاز الحالي",
                value = "${deviceHeading.roundToInt()}°",
                subtitle = QiblaCalculator.getCompassDirectionText(deviceHeading).first,
                icon = Icons.Default.Navigation,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "دقة المستشعر",
                value = sensorAccuracy.labelAr,
                subtitle = sensorAccuracy.labelEn,
                icon = Icons.Default.Sensors,
                tint = when (sensorAccuracy) {
                    SensorAccuracyLevel.HIGH -> IslamicMint
                    SensorAccuracyLevel.MEDIUM -> IslamicGold
                    else -> IslamicAmber
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
