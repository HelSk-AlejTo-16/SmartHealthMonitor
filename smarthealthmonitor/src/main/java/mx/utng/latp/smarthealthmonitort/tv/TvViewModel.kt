package mx.utng.latp.smarthealthmonitort.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.utng.latp.smarthealthmonitort.data.SmartHealthRepository
// LecturaFC ya está en este paquete por nuestro archivo FCCardPresenter/MockData

class TvViewModel : ViewModel() {

    // FC actual del wearable (o 0 si no hay dato)
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000), 
            0
        )

    // Historial de lecturas desde Room DAO (simulado temporalmente)
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}
