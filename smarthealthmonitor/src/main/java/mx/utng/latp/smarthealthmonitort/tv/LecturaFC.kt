package mx.utng.latp.smarthealthmonitort.tv

data class LecturaFC(
    val id: Int = 0,
    val bpm: Int,
    val estado: String = "Normal",
    val dispositivo: String = "tv",
    val hora: String,
    val sincronizado: Boolean = false
)
