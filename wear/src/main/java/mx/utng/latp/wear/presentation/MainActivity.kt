// wear/.../presentation/WearMainActivity.kt
package mx.utng.smarthealthmonitor.wear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.latp.wear.presentation.SmartHealthWearNavGraph
import mx.utng.latp.wear.presentation.healthMonitor.wear.HealthDataService
import mx.utng.latp.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {

    // ── Lanzadores de solicitud de permisos (Flujo de 2 pasos) ───────────────
    
    // Paso 2: Pedir permiso en segundo plano (Requerido en API 30+)
    private val backgroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            activarMonitoreo()
        } else {
            android.util.Log.w("WearMainActivity", "Permiso de sensores en segundo plano denegado.")
            // Aún podemos intentar el monitoreo, pero podría fallar con la pantalla apagada
            activarMonitoreo() 
        }
    }

    // Paso 1: Pedir permisos en primer plano
    private val foregroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            // Primer plano concedido → pedir segundo plano
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS_BACKGROUND) != PackageManager.PERMISSION_GRANTED) {
                    backgroundPermissionLauncher.launch(Manifest.permission.BODY_SENSORS_BACKGROUND)
                } else {
                    activarMonitoreo()
                }
            } else {
                activarMonitoreo()
            }
        } else {
            android.util.Log.e("WearMainActivity", "Permisos de primer plano denegados.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permisos de primer plano primero
        val permisosPrimerPlano = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        val faltantesPrimerPlano = permisosPrimerPlano.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (faltantesPrimerPlano.isNotEmpty()) {
            foregroundPermissionLauncher.launch(faltantesPrimerPlano.toTypedArray())
        } else {
            // Ya tenemos primer plano, checar segundo plano
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS_BACKGROUND) != PackageManager.PERMISSION_GRANTED) {
                    backgroundPermissionLauncher.launch(Manifest.permission.BODY_SENSORS_BACKGROUND)
                } else {
                    activarMonitoreo()
                }
            } else {
                activarMonitoreo()
            }
        }

        setContent {
            SmartHealthWearTheme {
                SmartHealthWearNavGraph()
            }
        }
    }

    /** Registra HealthDataService con Health Services para recepción pasiva de datos */
    private fun activarMonitoreo() {
        android.util.Log.i("WearMainActivity", "Permisos OK. Solicitando registro de monitoreo...")
        lifecycleScope.launch {
            HealthDataService.registrar(applicationContext)
        }
    }
}
