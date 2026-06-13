package mx.utng.latp.wear.presentation.healthMonitor.wear

/**
 * Singleton compartido entre HealthDataService (escribe) y WearApp composable (lee).
 *
 * Cuando el PassiveListenerService detecta un nuevo BPM del sensor del reloj,
 * escribe aquí. La UI del reloj escucha los callbacks onNuevoBpm / onNuevoPasos
 * y actualiza su estado local de Compose automáticamente.
 *
 * También guarda el último valor recibido para inicializar la UI cuando se abre la app.
 */
object WearHealthState {

    // Último BPM detectado por el sensor (0 = sin datos todavía)
    var ultimoBpm: Int = 0
        private set

    // Últimos pasos detectados (0 = sin datos todavía)
    var ultimoPasos: Int = 0
        private set

    // Callbacks que la UI registra para recibir actualizaciones reactivas
    var onNuevoBpm:   ((Int) -> Unit)? = null
    var onNuevoPasos: ((Int) -> Unit)? = null

    /** Llamado por HealthDataService cuando el sensor entrega un nuevo BPM */
    fun actualizarBpm(bpm: Int) {
        ultimoBpm = bpm
        onNuevoBpm?.invoke(bpm)
    }

    /** Llamado por HealthDataService cuando el sensor entrega nuevos pasos */
    fun actualizarPasos(pasos: Int) {
        ultimoPasos = pasos
        onNuevoPasos?.invoke(pasos)
    }
}
