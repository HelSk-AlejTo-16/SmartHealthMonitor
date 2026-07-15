package mx.utng.latp.smarthealthmonitort.tv

object MockData {
    val historialFC = listOf(
        LecturaFC(1, 78, "Normal", "wear", "11:00", true),
        LecturaFC(2, 82, "Normal", "app", "10:30", true),
        LecturaFC(3, 76, "Normal", "wear", "10:00", true),
        LecturaFC(4, 95, "FC Alta", "wear", "09:30", false),
        LecturaFC(5, 71, "Normal", "app", "09:00", true)
    )
}
