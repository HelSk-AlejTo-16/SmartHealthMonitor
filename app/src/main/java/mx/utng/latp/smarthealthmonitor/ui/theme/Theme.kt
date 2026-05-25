package mx.utng.latp.smarthealthmonitor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary         = SHPrimaryDark,
    onPrimary       = SHOnPrimaryDark,
    background      = SHBackgroundDark,
    surface         = SHSurfaceDark,
)



private val LightColorScheme = lightColorScheme(
    primary         = SHPrimary,
    onPrimary       = SHOnPrimary,
    primaryContainer= SHPrimaryContainer,
    secondary       = SHSecondary,
    error           = SHError,
    background      = SHBackground,
    surface         = SHSurface,
    onSurface       = SHOnSurface,
)


@Composable
fun SmartHealthMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography   = Typography,
        content      = content
    )
}
