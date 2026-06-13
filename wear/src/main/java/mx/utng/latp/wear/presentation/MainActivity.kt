package mx.utng.latp.wear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.*
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mx.utng.latp.wear.presentation.healthMonitor.wear.HealthDataService
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearDataSender
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearHealthState
import mx.utng.latp.wear.presentation.theme.SmartHealthMonitorTheme

class WearMainActivity : ComponentActivity() {

    private val TAG = "WearMainActivity"

    // ── SensorManager — lectura directa del hardware ───────────────────────────
    private lateinit var sensorManager: SensorManager
    private var sensorFC: Sensor?     = null
    private var sensorPasos: Sensor?  = null
    private lateinit var wearDataSender: WearDataSender

    /** Tiempo del último envío al teléfono — para no saturar el canal */
    private var ultimoEnvioMs = 0L
    private val INTERVALO_ENVIO_MS = 5_000L   // máximo 1 envío cada 5 segundos

    /** Paso base del TYPE_STEP_COUNTER (acumulado desde el último reinicio) */
    private var pasosBase = -1

    @OptIn(DelicateCoroutinesApi::class)
    private val sensorListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {

                Sensor.TYPE_HEART_RATE -> {
                    val bpm = event.values[0].toInt()
                    if (bpm <= 0) return

                    Log.d(TAG, "❤️ Sensor BPM: $bpm")
                    WearHealthState.actualizarBpm(bpm)   // actualiza la UI del reloj

                    // Enviar al teléfono (throttled cada 5 s)
                    val ahora = System.currentTimeMillis()
                    if (ahora - ultimoEnvioMs >= INTERVALO_ENVIO_MS) {
                        ultimoEnvioMs = ahora
                        GlobalScope.launch(Dispatchers.IO + SupervisorJob()) {
                            wearDataSender.enviarFC(bpm)
                        }
                    }
                }

                Sensor.TYPE_STEP_COUNTER -> {
                    val totalPasos = event.values[0].toInt()

                    // La primera vez guardamos la base para calcular pasos de esta sesión
                    if (pasosBase < 0) pasosBase = totalPasos

                    val pasosSesion = totalPasos - pasosBase
                    Log.d(TAG, "👟 Sensor Pasos sesión: $pasosSesion (total=$totalPasos)")
                    WearHealthState.actualizarPasos(pasosSesion)

                    // Enviar pasos también, con el mismo throttle
                    val ahora = System.currentTimeMillis()
                    if (ahora - ultimoEnvioMs >= INTERVALO_ENVIO_MS) {
                        GlobalScope.launch(Dispatchers.IO + SupervisorJob()) {
                            wearDataSender.enviarPasos(pasosSesion)
                        }
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "Precisión del sensor ${sensor.name}: $accuracy")
        }
    }

    // ── Permisos ───────────────────────────────────────────────────────────────
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val sensorGranted = permissions[Manifest.permission.BODY_SENSORS] ?: false
        val healthGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions["android.permission.health.READ_HEART_RATE"] ?: true
        } else true

        Log.d(TAG, "Permisos: SENSOR=$sensorGranted, HEALTH=$healthGranted")
        if (sensorGranted && healthGranted) solicitarPermisoBackground()
        else registrarHealthServices()
    }

    private val backgroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "BODY_SENSORS_BACKGROUND: $granted")
        registrarHealthServices()
    }

    // ── Ciclo de vida ──────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Inicializar SensorManager y los sensores
        sensorManager  = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorFC       = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorPasos    = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        wearDataSender = WearDataSender(applicationContext)

        if (sensorFC == null) Log.w(TAG, "⚠️ Este dispositivo no tiene sensor TYPE_HEART_RATE")
        if (sensorPasos == null) Log.w(TAG, "⚠️ Este dispositivo no tiene sensor TYPE_STEP_COUNTER")

        checkPermissions()
        setContent { WearApp() }
    }

    override fun onResume() {
        super.onResume()
        // Registrar sensores cuando la app está en primer plano
        sensorFC?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL   // ~5 segundos entre lecturas
            )
            Log.d(TAG, "✅ Sensor FC registrado")
        }
        sensorPasos?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(TAG, "✅ Sensor Pasos registrado")
        }
    }

    override fun onPause() {
        super.onPause()
        // Liberar sensores cuando la app va al fondo (la batería lo agradece)
        sensorManager.unregisterListener(sensorListener)
        Log.d(TAG, "⏸️ Sensores desregistrados (app en segundo plano)")
        // El PassiveListenerService toma el relevo en segundo plano
    }

    // ── Permisos helpers ───────────────────────────────────────────────────────
    private fun checkPermissions() {
        val toRequest = mutableListOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            toRequest.add("android.permission.health.READ_HEART_RATE")
        }
        val allGranted = toRequest.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!allGranted) permissionLauncher.launch(toRequest.toTypedArray())
        else solicitarPermisoBackground()
    }

    private fun solicitarPermisoBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasBg = ContextCompat.checkSelfPermission(
                this, Manifest.permission.BODY_SENSORS_BACKGROUND
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasBg) backgroundPermissionLauncher.launch(Manifest.permission.BODY_SENSORS_BACKGROUND)
            else registrarHealthServices()
        } else registrarHealthServices()
    }

    private fun registrarHealthServices() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "🚀 Registrando PassiveListenerService (segundo plano)...")
                HealthDataService.registrar(applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al registrar: ${e.message}")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UI del reloj — solo display, todo automático
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WearApp() {
    var bpm   by remember { mutableIntStateOf(WearHealthState.ultimoBpm) }
    var pasos by remember { mutableIntStateOf(WearHealthState.ultimoPasos) }

    LaunchedEffect(Unit) {
        WearHealthState.onNuevoBpm   = { nuevo -> bpm   = nuevo }
        WearHealthState.onNuevoPasos = { nuevo -> pasos = nuevo }
    }

    SmartHealthMonitorTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val spec      = rememberTransformationSpec()

            ScreenScaffold(scrollState = listState) { padding ->
                TransformingLazyColumn(contentPadding = padding, state = listState) {

                    item {
                        ListHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, spec),
                            transformation = SurfaceTransformation(spec)
                        ) {
                            Text(text = "SmartHealth", textAlign = TextAlign.Center)
                        }
                    }

                    // ── Frecuencia Cardíaca ─────────────────────────────────────
                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, spec),
                            transformation = SurfaceTransformation(spec)
                        ) {
                            Column(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text  = "❤️ Frec. Cardíaca",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                if (bpm > 0) {
                                    Row(
                                        verticalAlignment     = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text       = "$bpm",
                                            fontSize   = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text     = "bpm",
                                            fontSize = 14.sp,
                                            color    = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text  = "Midiendo…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // ── Pasos ───────────────────────────────────────────────────
                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, spec),
                            transformation = SurfaceTransformation(spec)
                        ) {
                            Column(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text  = "👟 Pasos del día",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                if (pasos > 0) {
                                    Row(
                                        verticalAlignment     = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text       = "%,d".format(pasos),
                                            fontSize   = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text     = "pasos",
                                            fontSize = 12.sp,
                                            color    = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text  = "Contando…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text      = "🔄 Actualizando cada ~5 s",
                            modifier  = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, spec)
                                .padding(top = 4.dp),
                            textAlign = TextAlign.Center,
                            style     = MaterialTheme.typography.labelSmall,
                            color     = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun DefaultPreview() {
    SmartHealthMonitorTheme { WearApp() }
}
