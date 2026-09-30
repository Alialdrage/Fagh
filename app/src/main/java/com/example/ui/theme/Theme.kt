package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IslamicMint,
    onPrimary = IslamicEmeraldDark,
    primaryContainer = IslamicEmerald,
    onPrimaryContainer = IslamicMintSoft,
    secondary = IslamicGold,
    onSecondary = KaabaBlack,
    secondaryContainer = NightSurfaceVariant,
    onSecondaryContainer = IslamicGoldLight,
    tertiary = IslamicAmber,
    onTertiary = KaabaBlack,
    background = NightBackground,
    onBackground = TextPrimaryDark,
    surface = NightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = NightBorder,
    error = AccentRed
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicEmerald,
    onPrimary = Color.White,
    primaryContainer = IslamicEmeraldLight.copy(alpha = 0.2f),
    onPrimaryContainer = IslamicEmeraldDark,
    secondary = IslamicGoldDark,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldLight.copy(alpha = 0.25f),
    onSecondaryContainer = IslamicGoldDark,
    tertiary = IslamicAmber,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    error = AccentRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional themed colors for Islamic aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
