package mx.utng.latp.smarthealthmonitor.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.utng.latp.smarthealthmonitor.ui.screens.DashboardScreen
import mx.utng.latp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.utng.smarthealthmonitor.LoginScreen

@Composable
fun SmartHealthNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        // ── Login ──────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true  // eliminar Login del back stack
                        }
                    }
                }
            )
        }
        // ── Dashboard ──────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onHistorialClick = {
                    navController.navigate(Screen.Historial.route)
                },
                onAlertClick = {
                    navController.navigate(Screen.Alerta.route)
                }
            )
        }
        // ── Historial ──────────────────────────────────
        composable(Screen.Historial.route) {
            // TODO Ejercicio extra: HistorialScreen completo
            // Por ahora: pantalla temporal con Back
            PantallaEnConstruccion(
                titulo = "Historial completo",
                onBack = { navController.popBackStack() }
            )
        }
        // ── Alerta ─────────────────────────────────────
        composable(Screen.Alerta.route) {
            PantallaEnConstruccion(
                titulo = "Enviar alerta",
                onBack = { navController.popBackStack() }
            )
        }
    }
}


// Pantalla temporal para destinos no implementados aún
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEnConstruccion(titulo: String, onBack: () -> Unit) {
    SmartHealthMonitorTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar")
                    }
                }
            )
        }) { pad ->
            Box(
                Modifier.fillMaxSize().padding(pad),
                contentAlignment = Alignment.Center) {
                Text("Próximamente: $titulo",
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

