package mx.utng.latp.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearHealthState
import mx.utng.latp.wear.presentation.model.LecturaFC

/**
 * ViewModel del dashboard del reloj.
 *
 * Observa WearHealthState (equivalente local a SmartHealthRepository del módulo :app
 * — no se puede importar directamente porque ambos son módulos :application
 * y AGP 8.x lo rechaza como dynamic features inválido).
 */
class WearDashboardViewModel : ViewModel() {

    // Frecuencia cardíaca actual del sensor del reloj.
    // Si el sensor aún no ha entregado datos (valor 0), muestra 72 bpm por defecto.
    val fc: StateFlow<Int> = WearHealthState.fcFlow
        .map { if (it == 0) 72 else it }   // valor por defecto mientras el sensor calienta
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 72
        )

    // Pasos del día
    val pasos: StateFlow<Int> = WearHealthState.pasosFlow
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    // ← NUEVO: historial de lecturas desde WearHealthState
    // (equivalente a SmartHealthRepository.obtenerHistorial() del módulo :app)
    val historial: StateFlow<List<LecturaFC>> =
        WearHealthState.historialFlow
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val mqttPublisher = mx.utng.latp.wear.mqtt.MqttWearPublisher()

    init {
        mqttPublisher.connect()
        viewModelScope.launch {
            fc.collect { bpm ->
                val estado = when {
                    bpm < 60 -> "FC Baja"
                    bpm > 100 -> "FC Alta"
                    else -> "Normal"
                }
                mqttPublisher.publishFC(bpm, estado)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttPublisher.disconnect()
    }
}
