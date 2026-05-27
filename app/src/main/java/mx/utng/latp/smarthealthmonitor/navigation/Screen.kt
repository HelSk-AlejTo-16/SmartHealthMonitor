package mx.utng.latp.smarthealthmonitor.navigation


sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Dashboard : Screen("dashboard")
    object Historial : Screen("historial")
    object Alerta    : Screen("alerta")
}

// ¿Por qué sealed class y no constantes String?
// Type-safe: Screen.Login.route nunca tiene typos.
// Exhaustivo: el compilador detecta si falta un destino.
