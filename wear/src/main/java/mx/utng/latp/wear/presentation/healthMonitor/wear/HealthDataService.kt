package mx.utng.latp.wear.presentation.healthMonitor.wear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await
import mx.utng.latp.wear.watchface.WearBpmStore

class HealthDataService : PassiveListenerService() {

    private lateinit var wearDataSender: WearDataSender

    override fun onCreate() {
        super.onCreate()
        wearDataSender = WearDataSender(this.applicationContext)
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        Log.d("HealthDataService", "💓 onNewDataPointsReceived: Recibidos ${fcDataPoints.size} puntos de FC")

        if (fcDataPoints.isEmpty()) {
            Log.w("HealthDataService", "⚠️ No hay puntos de FC en el contenedor")
        }

        fcDataPoints.forEach { dataPoint ->
            try {
                val bpm = (dataPoint.value as Number).toInt()
                Log.i("HealthDataService", "✅ BPM detectado: $bpm")

                // 1. Actualizar el estado compartido para que la UI del reloj lo refleje
                WearHealthState.actualizarBpm(bpm)

                // 2. Persistir en SharedPreferences para que el Watch Face lo lea
                WearBpmStore.guardar(applicationContext, bpm)

                // 3. Enviar al teléfono en background
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch(Dispatchers.IO + SupervisorJob()) {
                    Log.d("HealthDataService", "📡 Enviando $bpm bpm al teléfono...")
                    wearDataSender.enviarFC(bpm)
                }
            } catch (e: Exception) {
                Log.e("HealthDataService", "❌ Error al procesar punto de datos", e)
            }
        }
    }


    // ✅ FIX: onDestroy eliminado — ya no hay scope de instancia que cancelar.
    //    GlobalScope es gestionado por el proceso de la aplicación, no por el servicio.


    companion object {
        suspend fun registrar(context: Context) {
            Log.d("HealthDataService", "🚀 Iniciando registro de servicio pasivo...")

            // 1. Verificar permisos antes de intentar registrar
            val hasBodySensors = ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
            val hasActivityRecognition = ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
            
            // BODY_SENSORS_BACKGROUND es necesario en API 33+ para monitoreo pasivo
            val hasBackgroundSensors = if (android.os.Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(context, "android.permission.BODY_SENSORS_BACKGROUND") == PackageManager.PERMISSION_GRANTED
            } else true

            if (!hasBodySensors) {
                Log.e("HealthDataService", "❌ ABORTANDO: Falta permiso BODY_SENSORS")
                return
            }
            if (!hasActivityRecognition) {
                Log.e("HealthDataService", "❌ ABORTANDO: Falta permiso ACTIVITY_RECOGNITION")
                return
            }
            if (!hasBackgroundSensors) {
                Log.w("HealthDataService", "⚠️ Falta permiso BODY_SENSORS_BACKGROUND. El monitoreo podría fallar con la pantalla apagada.")
            }

            val hsClient = HealthServices.getClient(context)
            val passiveClient = hsClient.passiveMonitoringClient

            // 2. Verificar si el dispositivo soporta FC
            val capabilities = try {
                passiveClient.getCapabilitiesAsync().await()
            } catch (e: Exception) {
                Log.e("HealthDataService", "❌ Error al obtener capacidades", e)
                null
            }

            val supportsHeartRate = capabilities?.supportedDataTypesPassiveMonitoring?.contains(DataType.HEART_RATE_BPM) ?: false
            if (!supportsHeartRate) {
                Log.e("HealthDataService", "❌ El dispositivo no soporta monitoreo pasivo de FC")
                return
            }

            val config = PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .build()

            try {
                passiveClient.setPassiveListenerServiceAsync(
                    HealthDataService::class.java,
                    config
                ).await()
                Log.i("HealthDataService", "🎉 Registro EXITOSO en Health Services")
            } catch (e: SecurityException) {
                Log.e("HealthDataService", "❌ Error de SEGURIDAD (Permisos): ${e.message}")
            } catch (e: Exception) {
                Log.e("HealthDataService", "❌ Error FATAL al registrar servicio", e)
                e.printStackTrace()
            }
        }
    }
}
