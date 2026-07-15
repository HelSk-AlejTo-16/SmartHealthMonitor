package mx.utng.latp.smarthealthmonitort.tv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.utng.latp.smarthealthmonitort.data.SmartHealthRepository
import mx.utng.latp.smarthealthmonitort.data.TvNeonRepository
import mx.utng.latp.smarthealthmonitort.tv.domain.model.TvUiState

class TvViewModel(
    private val repository: SmartHealthRepository
) : ViewModel() {

    private val neonRepo = TvNeonRepository()
    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<mx.utng.latp.smarthealthmonitort.mqtt.TvMessage?>(null)
    private val mqttSubscriber = mx.utng.latp.smarthealthmonitort.mqtt.MqttTvSubscriber(mqttFlow)

    init {
        cargarDatos()
        
        mqttSubscriber.connect()

        // Observar mensajes MQTT y actualizar el estado de la UI
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                _state.update { it.copy(
                    fcActual = tvMsg.bpm,
                    fcEstado = tvMsg.estado,
                    ultimaHora = tvMsg.hora,
                    isLoading = false
                )}
            }
        }
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading=true) }
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                val stats    = neonRepo.obtenerEstadisticas()
                _state.update { it.copy(
                    lecturas  = lecturas.map { dto -> dto.toLecturaFC() },
                    estadisticas = stats.map { dto -> dto.toLecturaFC() },
                    isLoading = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(error=e.message, isLoading=false) }
            }
        }
    }
    
    fun refresh() = cargarDatos()

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}
