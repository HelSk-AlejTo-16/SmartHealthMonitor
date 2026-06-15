package mx.utng.latp.smarthealthmonitor.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WearListenerService : WearableListenerService() {

    // BUG #5 CORREGIDO: Se cambia Dispatchers.Main por Dispatchers.IO.
    // WearableListenerService corre en background sin contexto de UI,
    // usar Main podía causar que las actualizaciones al repositorio fallaran.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "WearListenerService"
        private const val PATH_FC = "/smarthealthmonitor/fc"
        private const val PATH_PASOS = "/smarthealthmonitor/pasos"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Log inmediato para debug - esto DEBE aparecer si la conexión física existe
        Log.i(TAG, "🔔onMessageReceived ejecutado!")
        
        val path = messageEvent.path
        val data = String(messageEvent.data)
        
        Log.i(TAG, "📥 MENSAJE RECIBIDO -> Path: $path | Data: $data")

        // Usamos el scope para asegurar que la actualización del repositorio sea detectada por el ViewModel
        scope.launch {
            try {
                when (path) {
                    PATH_FC -> {
                        val bpm = data.toIntOrNull()
                        if (bpm != null) {
                            Log.d(TAG, "💓 Actualizando Repositorio: $bpm BPM")
                            SmartHealthRepository.actualizarFC(bpm)
                        }
                    }
                    PATH_PASOS -> {
                        val pasos = data.toIntOrNull()
                        if (pasos != null) {
                            Log.d(TAG, "👟 Actualizando Repositorio: $pasos")
                            SmartHealthRepository.actualizarPasos(pasos)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error procesando mensaje: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
