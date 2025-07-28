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
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Defining the custom colors here
val CustomAppPrimary = Color(0xFF9E3B2F) // This is the icons color.
val CustomOnPrimaryText = Color(0xFFFFFFFF) // This is the text color.
val CustomBackground = Color(0xFFF9E3B2) // This is the background color.



private val DarkColorScheme =
    darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
    lightColorScheme(
        primary = CustomAppPrimary,
        secondary = InventorySecondary,
        tertiary = InventoryAccent,
        error = ErrorRed,
        background = CustomBackground, // new background color
        surface = InventoryCard,      // Kept as is, since it's defined

        onPrimary = CustomOnPrimaryText,
        onSecondary = androidx.compose.ui.graphics.Color.Black, // Text on InventorySecondary
        onTertiary = androidx.compose.ui.graphics.Color.Black,  // Text on InventoryAccent
        onError = androidx.compose.ui.graphics.Color.White,     // Text on ErrorRed
        onBackground = androidx.compose.ui.graphics.Color.Black,
        onSurface = androidx.compose.ui.graphics.Color.Black,   // Text on InventoryCard

        // We might want to theme these container colors as well to match new scheme. Thought for later?
        primaryContainer = Color(0xFFE3F2FD),
        onPrimaryContainer = Color(0xFF001F2A),

        secondaryContainer = Color(0xFFE8F5E8), // Default was light green
        onSecondaryContainer = Color(0xFF1B5E20),

        errorContainer = Color(0xFFFFEBEE), // Default light pink
        onErrorContainer = Color(0xFFB71C1C)
    )

@Composable
fun IStockInventoryManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // changed it to false to prevent overriding of custom colors
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
            window.statusBarColor = colorScheme.primary.toArgb() // Status bar will now use new primary color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
