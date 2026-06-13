package mx.utng.latp.smarthealthmonitor.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.utng.latp.smarthealthmonitor.data.SmartHealthRepository
import mx.utng.latp.smarthealthmonitor.data.models.MockData

class DashboardViewModel : ViewModel() {

    // Escuchamos el repositorio.
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .onEach { Log.d("DashboardViewModel", "📱 UI Recibió FC: $it") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .onEach { Log.d("DashboardViewModel", "📱 UI Recibió Pasos: $it") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    // Recuperamos la propiedad historial que necesita la pantalla
    val historial = MockData.historialFC
}
