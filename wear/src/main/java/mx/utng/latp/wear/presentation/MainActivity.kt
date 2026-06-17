// wear/.../presentation/WearMainActivity.kt
package mx.utng.smarthealthmonitor.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.utng.latp.wear.presentation.SmartHealthWearNavGraph
import mx.utng.latp.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthWearTheme {
                SmartHealthWearNavGraph()
            }
        }
    }
}
