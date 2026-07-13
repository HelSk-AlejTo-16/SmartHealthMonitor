package mx.utng.latp.smarthealthmonitor.data

import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import mx.utng.latp.smarthealthmonitor.data.db.LecturaFC
import mx.utng.latp.smarthealthmonitor.data.db.LecturaFCDao
import mx.utng.latp.smarthealthmonitor.data.db.SmartHealthDB

/**
 * Repositorio singleton que centraliza los datos de salud.
 * El WearListenerService escribe aquí.
 * El ViewModel lee de aquí.
 */
object SmartHealthRepository {
    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    internal val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    private var dao: LecturaFCDao? = null

    fun init(context: Context) {
        dao = SmartHealthDB.getDatabase(context).lecturaDao()
    }

    suspend fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm
        // Persistir en Room automáticamente
        dao?.insertar(LecturaFC(valorBpm = bpm))
    }

    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    // Flow del historial desde Room
    fun obtenerHistorial(): Flow<List<LecturaFC>> =
        dao?.obtenerUltimas() ?: emptyFlow()
}
//comentario

// En Application.kt (crear si no existe):
class SmartHealthApp : Application() {
    lateinit var mqttService: mx.utng.latp.smarthealthmonitor.mqtt.MqttAppService

    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)  // inicializar Room
        
        // Inicializar MQTT con el MutableStateFlow del Repository
        mqttService = mx.utng.latp.smarthealthmonitor.mqtt.MqttAppService(
            context = this,
            fcFlow  = SmartHealthRepository._fcFlow
        )
        mqttService.connect()
    }
}