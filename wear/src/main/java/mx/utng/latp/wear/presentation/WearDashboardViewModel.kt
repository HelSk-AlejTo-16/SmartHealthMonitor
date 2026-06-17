package mx.utng.latp.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.utng.latp.wear.presentation.healthMonitor.wear.WearHealthState

/**
 * ViewModel del dashboard del reloj.
 *
 * Observa WearHealthState.fcFlow (equivalente local a SmartHealthRepository.fcFlow
 * del módulo :app — no se puede importar directamente porque ambos son módulos
 * :application y AGP 8.x lo rechaza como dynamic features inválido).
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

    // Pasos del día (disponible para pantallas que lo necesiten)
    val pasos: StateFlow<Int> = WearHealthState.pasosFlow
        .map { it }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )
}
