package mx.utng.latp.smarthealthmonitort.tv.domain.model

import mx.utng.latp.smarthealthmonitort.tv.LecturaFC

data class TvUiState(
    val lecturas: List<LecturaFC> = emptyList(),
    val estadisticas: List<LecturaFC> = emptyList(),
    val fcActual: Int = 0,
    val fcEstado: String = "Normal",
    val ultimaHora: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
)
