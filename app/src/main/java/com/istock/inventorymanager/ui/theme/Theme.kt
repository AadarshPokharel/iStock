package com.istock.inventorymanager.ui.theme

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

private val DarkColorScheme =
        darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
        lightColorScheme(
                primary = InventoryPrimary,
                secondary = InventorySecondary,
                tertiary = InventoryAccent,
                error = ErrorRed,
                background = InventoryBackground,
                surface = InventoryCard,
                onPrimary = androidx.compose.ui.graphics.Color.White,
                onSecondary = androidx.compose.ui.graphics.Color.White,
                onTertiary = androidx.compose.ui.graphics.Color.White,
                onError = androidx.compose.ui.graphics.Color.White,
                onBackground = androidx.compose.ui.graphics.Color.Black,
                onSurface = androidx.compose.ui.graphics.Color.Black,
                primaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF0D47A1),
                secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8F5E8),
                onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1B5E20),
                errorContainer = androidx.compose.ui.graphics.Color(0xFFFFEBEE),
                onErrorContainer = androidx.compose.ui.graphics.Color(0xFFB71C1C)
        )

@Composable
fun IStockInventoryManagerTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = true,
        content: @Composable () -> Unit
) {
    val colorScheme =
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
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

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
