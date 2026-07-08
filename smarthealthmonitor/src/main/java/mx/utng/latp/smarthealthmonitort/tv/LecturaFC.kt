package mx.utng.latp.smarthealthmonitort.tv

data class LecturaFC(
    val id: Int,
    val valorBpm: Int,
    val hora: String,
    val esNormal: Boolean = true
)
