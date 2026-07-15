package mx.utng.latp.smarthealthmonitort.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.utng.latp.smarthealthmonitort.data.remote.LecturaFcDto
import mx.utng.latp.smarthealthmonitort.data.remote.NeonClient
import mx.utng.latp.smarthealthmonitort.data.remote.NeonRequest

class TvNeonRepository {
 
    /** Obtener historial completo de los 3 dispositivos */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                auth    = NeonClient.AUTH_HEADER,
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT id,bpm,estado,dispositivo,hora,created_at
                               FROM lecturas_fc
                               ORDER BY created_at DESC
                               LIMIT $1""".trimIndent(),
                    params = listOf(limite)
                )
            ).rows
        }
 
    /** Estadísticas por dispositivo */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                auth    = NeonClient.AUTH_HEADER,
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query  = """SELECT 0 as id,
                               ROUND(AVG(bpm)) AS bpm,
                               'Promedio' AS estado,
                               dispositivo,
                               MAX(hora) AS hora
                               FROM lecturas_fc
                               GROUP BY dispositivo""".trimIndent()
                )
            ).rows
        }
}
