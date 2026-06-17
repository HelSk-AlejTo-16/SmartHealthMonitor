package mx.utng.smarthealthmonitor.wear.watchface

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mx.utng.latp.wear.presentation.healthMonitor.wear.HealthDataService
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearHealthState
import mx.utng.latp.wear.watchface.WearBpmStore

class SmartHealthWatchFaceService : WatchFaceService() {

    private val TAG = "SmartHealthWatchFace"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // ── Sensor de frecuencia cardíaca ─────────────────────────────────────────
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null

    private val heartRateListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                val bpm = event.values[0].toInt()
                if (bpm > 0) {
                    Log.d(TAG, "❤️ WatchFace BPM: $bpm")
                    WearHealthState.actualizarBpm(bpm)
                    WearBpmStore.guardar(applicationContext, bpm)  // ← persistir
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "Precisión sensor FC: $accuracy")
        }
    }

    // ── Ciclo de vida del servicio ────────────────────────────────────────────
    override fun onCreate() {
        super.onCreate()
        sensorManager  = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            iniciarSensores()
        } else {
            Log.w(TAG, "⚠️ Permisos de sensores no concedidos. El WatchFace esperará a que el usuario abra la App.")
        }
    }

    private fun iniciarSensores() {
        heartRateSensor?.let { sensor ->
            sensorManager.registerListener(
                heartRateListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(TAG, "✅ Sensor FC registrado en WatchFace")
        } ?: Log.w(TAG, "⚠️ No hay sensor TYPE_HEART_RATE en este dispositivo")

        // Activar monitoreo pasivo (Health Services) para recibir FC en background
        serviceScope.launch {
            HealthDataService.registrar(applicationContext)
            Log.d(TAG, "✅ Intento de registro de monitoreo pasivo desde WatchFace completado")
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(heartRateListener)
        serviceScope.cancel()
        Log.d(TAG, "⏹️ Sensor FC y scope liberados")
        super.onDestroy()
    }

    // ── Crear el watch face ───────────────────────────────────────────────────
    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = SmartHealthRenderer(
            context                              = applicationContext,
            surfaceHolder                        = surfaceHolder,
            watchState                           = watchState,
            complicationSlotsManager             = complicationSlotsManager,
            currentUserStyleRepository           = currentUserStyleRepository,
            interactiveDrawModeUpdateDelayMillis = 1_000L
        )
        return WatchFace(WatchFaceType.DIGITAL, renderer)
    }
}
