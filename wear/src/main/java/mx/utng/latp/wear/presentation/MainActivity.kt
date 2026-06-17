// wear/.../presentation/WearMainActivity.kt
package mx.utng.smarthealthmonitor.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.navigation.WearNavigator
import mx.utng.latp.wear.presentation.WearDashboardScreen
import mx.utng.latp.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthWearTheme {
                // TODO Ej.02: reemplazar con WearNavGraph
                WearDashboardScreen()

            }
        }
    }
}
