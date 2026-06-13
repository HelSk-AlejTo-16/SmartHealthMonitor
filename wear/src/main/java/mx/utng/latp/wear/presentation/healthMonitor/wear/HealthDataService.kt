package mx.utng.latp.wear.presentation.healthMonitor.wear

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await

class HealthDataService : PassiveListenerService() {

    private lateinit var wearDataSender: WearDataSender

    override fun onCreate() {
        super.onCreate()
        wearDataSender = WearDataSender(this.applicationContext)
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        Log.d("HealthDataService", "Recibidos ${fcDataPoints.size} puntos de FC")

        fcDataPoints.forEach { dataPoint ->
            try {
                val bpm = (dataPoint.value as Number).toInt()
                Log.i("HealthDataService", "Procesando BPM: $bpm")

                // 1. Actualizar el estado compartido para que la UI del reloj lo refleje
                WearHealthState.actualizarBpm(bpm)

                // 2. Enviar al teléfono en background (GlobalScope para sobrevivir al onDestroy)
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch(Dispatchers.IO + SupervisorJob()) {
                    wearDataSender.enviarFC(bpm)
                }
            } catch (e: Exception) {
                Log.e("HealthDataService", "Error al procesar punto de datos", e)
            }
        }
    }


    // ✅ FIX: onDestroy eliminado — ya no hay scope de instancia que cancelar.
    //    GlobalScope es gestionado por el proceso de la aplicación, no por el servicio.


    companion object {
        suspend fun registrar(context: Context) {
            Log.d("HealthDataService", "Intentando registrar servicio pasivo...")
            val hsClient = HealthServices.getClient(context)
            val passiveClient = hsClient.passiveMonitoringClient

            val config = PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .build()

            try {
                passiveClient.setPassiveListenerServiceAsync(
                    HealthDataService::class.java,
                    config
                ).await()
                Log.i("HealthDataService", "Servicio pasivo registrado correctamente")
            } catch (e: Exception) {
                Log.e("HealthDataService", "Fallo al registrar servicio pasivo", e)
            }
        }
    }
}
