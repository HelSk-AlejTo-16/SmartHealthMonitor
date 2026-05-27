package mx.utng.latp.smarthealthmonitor

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.utng.latp.smarthealthmonitor.navigation.SmartHealthNavGraph
import mx.utng.latp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.utng.smarthealthmonitor.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // NavGraph es ahora el punto de entrada — no LoginScreen directamente
            SmartHealthNavGraph()
        }
    }





    @Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ThemePreview() {
    SmartHealthMonitorTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "SmartHealth Monitor",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(name = "Login - Light", showBackground = true,
    showSystemUi = true, device = "id:pixel_6")
@Preview(name = "Login - Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Login - Big Font", showBackground = true,
    fontScale = 1.5f)
@Composable
private fun LoginScreenPreview() {
    SmartHealthMonitorTheme {
        LoginScreen()
    }
}}
