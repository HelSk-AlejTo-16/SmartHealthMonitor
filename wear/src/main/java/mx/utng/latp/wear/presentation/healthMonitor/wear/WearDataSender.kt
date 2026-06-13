package mx.utng.latp.wear.presentation.healthMonitor.wear

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WearDataSender(private val context: Context) {

    companion object {
        private const val TAG = "WearDataSender"
        private const val PATH_FC = "/smarthealthmonitor/fc"
        private const val PATH_PASOS = "/smarthealthmonitor/pasos"
        private const val CAPABILITY_NAME = "health_monitor_receiver"
    }

    suspend fun enviarFC(bpm: Int) {
        Log.i(TAG, "📤 Enviando Ritmo Cardiaco: $bpm BPM")
        enviarMensaje(PATH_FC, bpm.toString())
    }

    suspend fun enviarPasos(pasos: Int) {
        Log.i(TAG, "📤 Enviando Pasos: $pasos")
        enviarMensaje(PATH_PASOS, pasos.toString())
    }

    private suspend fun enviarMensaje(path: String, data: String) {
        try {
            // ── DIAGNÓSTICO PASO 1: Capability ──────────────────────────────────────
            Log.d(TAG, "🔍 [DIAG] Buscando nodos con capacidad: $CAPABILITY_NAME")

            val capabilityInfo = Wearable.getCapabilityClient(context)
                .getCapability(CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE)
                .await()

            val nodes = capabilityInfo.nodes
            Log.d(TAG, "📍 [DIAG] Nodos con capability '$CAPABILITY_NAME': ${nodes.size}")
            nodes.forEach { n ->
                Log.d(TAG, "     → Nodo: '${n.displayName}' | ID: ${n.id} | nearby=${n.isNearby}")
            }

            val targetNodes = nodes.toMutableSet()

            // ── DIAGNÓSTICO PASO 2: Fallback a todos los nodos conectados ────────────
            if (targetNodes.isEmpty()) {
                Log.w(TAG, "⚠️ [DIAG] 0 nodos con capability. Causa probable: " +
                        "app del teléfono no instalada, vinculación incompleta, " +
                        "o Google Pixel Watch app con error.")

                val allNodes = Wearable.getNodeClient(context).connectedNodes.await()
                Log.d(TAG, "📍 [DIAG] Nodos conectados en total (fallback): ${allNodes.size}")

                if (allNodes.isEmpty()) {
                    Log.e(TAG, "❌ [DIAG] 0 nodos conectados. " +
                            "Verifica: 1) Bluetooth activo, 2) App Pixel Watch sin errores, " +
                            "3) Misma cuenta Google en reloj y teléfono.")
                } else {
                    allNodes.forEach { n ->
                        Log.d(TAG, "     → Nodo fallback: '${n.displayName}' | nearby=${n.isNearby}")
                    }
                }
                targetNodes.addAll(allNodes)
            }

            // ── Sin destino: abortar ─────────────────────────────────────────────────
            if (targetNodes.isEmpty()) {
                Log.e(TAG, "🚫 [DIAG] Envío cancelado — ningún nodo disponible. " +
                        "El reloj y el teléfono NO están vinculados correctamente.")
                return
            }

            // ── DIAGNÓSTICO PASO 3: Envío ────────────────────────────────────────────
            targetNodes.forEach { node ->
                try {
                    Log.d(TAG, "🚀 Enviando '$path' → '${node.displayName}' (${node.id})")
                    Wearable.getMessageClient(context).sendMessage(
                        node.id,
                        path,
                        data.toByteArray()
                    ).await()
                    Log.i(TAG, "✅ Mensaje enviado con éxito a '${node.displayName}'")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Fallo al enviar a '${node.displayName}': ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "💀 Error crítico en enviarMensaje: ${e.message}", e)
        }
    }
}
