package mx.utng.latp.smarthealthmonitort.tv

object MockData {
    val historialFC = listOf(
        LecturaFC(id = 2, valorBpm = 75, hora = "Hace 1 hr"),
        LecturaFC(id = 3, valorBpm = 82, hora = "Hace 2 hrs"),
        LecturaFC(id = 4, valorBpm = 95, hora = "Hace 3 hrs", esNormal = false),
        LecturaFC(id = 5, valorBpm = 68, hora = "Hace 4 hrs")
    )
}
