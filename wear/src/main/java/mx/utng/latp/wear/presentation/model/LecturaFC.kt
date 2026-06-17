package mx.utng.latp.wear.presentation.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Versión local de LecturaFC para el módulo :wear.
 *
 * El módulo :wear no puede importar LecturaFC del módulo :app porque ambos son
 * módulos :application y AGP 8.x lo rechaza (error de dynamic features).
 * Esta clase replica la misma estructura para que WearHistorialScreen compile.
 */
data class LecturaFC(
    val id: Long = System.currentTimeMillis(),
    val valorBpm: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    /** true si la FC está en rango normal (60–100 bpm) */
    val esNormal: Boolean get() = valorBpm in 60..100

    /** Hora formateada como HH:mm:ss para mostrar en la lista */
    val hora: String get() {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
