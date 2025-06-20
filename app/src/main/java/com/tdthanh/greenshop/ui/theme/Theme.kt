package com.tdthanh.greenshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green60,
    onPrimary = White,
    primaryContainer = Green20,
    onPrimaryContainer = Green80,
    
    secondary = FreshGreen,
    onSecondary = White,
    secondaryContainer = LimeGreen,
    onSecondaryContainer = Grey90,
    
    tertiary = Mint,
    onTertiary = White,
    
    background = Grey90,
    onBackground = White,
    surface = Grey80,
    onSurface = White,
    
    surfaceVariant = Grey60,
    onSurfaceVariant = Grey20,
    
    outline = Grey40,
    outlineVariant = Grey60,
    
    error = ErrorRed,
    onError = White,
    errorContainer = Red80,
    onErrorContainer = White
)

private val LightColorScheme = lightColorScheme(
    primary = Green80,
    onPrimary = White,
    primaryContainer = LightGreen,
    onPrimaryContainer = Green20,
    
    secondary = FreshGreen,
    onSecondary = White,
    secondaryContainer = VeryLightGreen,
    onSecondaryContainer = Green40,
    
    tertiary = Mint,
    onTertiary = White,
    tertiaryContainer = LightGreen,
    onTertiaryContainer = Green20,
    
    background = White,
    onBackground = Grey90,
    surface = White,
    onSurface = Grey90,
    
    surfaceVariant = Grey10,
    onSurfaceVariant = Grey80,
    
    outline = Grey40,
    outlineVariant = Grey20,
    
    error = ErrorRed,
    onError = White,
    errorContainer = Grey10,
    onErrorContainer = ErrorRed,
    
    // Additional colors for better UX
    inverseSurface = Grey90,
    inverseOnSurface = White,
    inversePrimary = Green60,
    
    surfaceTint = Green80,
    scrim = Grey90.copy(alpha = 0.5f)
)

@Composable
fun GreenShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled for consistent green branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
