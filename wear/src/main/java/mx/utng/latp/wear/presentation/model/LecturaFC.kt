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
    val id: Int = 0,
    val bpm: Int,
    val estado: String = "Normal",
    val dispositivo: String = "wear",
    val hora: String,
    val sincronizado: Boolean = false
)
