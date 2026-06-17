package mx.utng.latp.wear.presentation.healthMonitor.wear

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio de estado de salud local del reloj.
 *
 * Equivalente al SmartHealthRepository del módulo :app, pero para el lado del reloj.
 * El módulo :wear NO puede importar directamente del módulo :app (ambos son :application,
 * y AGP 8.x lo interpreta como dynamic features → error de build).
 *
 * Flujo de datos:
 *   Sensor (HealthDataService / SensorManager)
 *      → WearHealthState.actualizarBpm()
 *      → fcFlow emite nuevo valor
 *      → WearDashboardViewModel lo observa y actualiza la UI
 *      → WearDataSender envía el valor al teléfono vía Wearable API
 */
object WearHealthState {

    // ── StateFlows observables por el ViewModel ───────────────────────────────
    private val _fcFlow    = MutableStateFlow(0)
    val fcFlow:    StateFlow<Int> = _fcFlow.asStateFlow()

    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    // ── Acceso directo al último valor (para inicializar la UI) ───────────────
    val ultimoBpm:   Int get() = _fcFlow.value
    val ultimoPasos: Int get() = _pasosFlow.value

    // ── Callbacks para la UI Compose (compatibilidad con LaunchedEffect) ──────
    var onNuevoBpm:   ((Int) -> Unit)? = null
    var onNuevoPasos: ((Int) -> Unit)? = null

    /** Llamado por HealthDataService / SensorManager cuando hay nuevo BPM */
    fun actualizarBpm(bpm: Int) {
        _fcFlow.value = bpm
        onNuevoBpm?.invoke(bpm)
    }

    /** Llamado por HealthDataService / SensorManager cuando hay nuevos pasos */
    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
        onNuevoPasos?.invoke(pasos)
    }
}
