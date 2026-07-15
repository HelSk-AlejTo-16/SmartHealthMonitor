package mx.utng.latp.smarthealthmonitor.data.models

import mx.utng.latp.smarthealthmonitor.data.db.LecturaFC
// Datos de prueba para desarrollo (mock data)
object MockData {
    val historialFC = listOf(
        LecturaFC(1, 78, "Normal", "app", "11:00", true),
        LecturaFC(2, 82, "Normal", "wear", "10:30", true),
        LecturaFC(3, 76, "Normal", "wear", "10:00", true),
        LecturaFC(4, 95, "FC Alta", "wear", "09:30", false),  // fuera de rango
        LecturaFC(5, 71, "Normal", "app", "09:00", true),
        LecturaFC(6, 80, "Normal", "wear", "08:30", true),
        LecturaFC(7, 74, "Normal", "tv", "08:00", true)
    )
    var fcActual = 78
    var pasosActual = 4250
}

