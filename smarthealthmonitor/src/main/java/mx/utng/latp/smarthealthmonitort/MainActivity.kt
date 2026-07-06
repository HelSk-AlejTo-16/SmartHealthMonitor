package mx.utng.latp.smarthealthmonitort

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import mx.utng.latp.smarthealthmonitort.ui.theme.SmartHealthMonitorTheme

/**
 * MainActivity para Android TV.
 * Es solo el contenedor: carga MainFragment.
 * TODA la lógica de UI va en el Fragment.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commit()
        }
    }
}
