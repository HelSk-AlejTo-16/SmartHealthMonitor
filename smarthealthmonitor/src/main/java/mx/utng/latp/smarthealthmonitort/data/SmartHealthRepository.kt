package mx.utng.latp.smarthealthmonitort.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import mx.utng.latp.smarthealthmonitort.tv.LecturaFC
import mx.utng.latp.smarthealthmonitort.tv.MockData

/**
 * STUB: Repositorio temporal para el módulo de TV.
 * Como el módulo TV no puede importar el módulo App directamente (restricción de Gradle),
 * simulamos el repositorio aquí para que la app compile y puedas probar la interfaz.
 */
class SmartHealthRepository {
    val fcActual = MutableStateFlow(88) // Valor simulado

    fun obtenerHistorial(): Flow<List<LecturaFC>> {
        return flowOf(MockData.historialFC)
    }
}
