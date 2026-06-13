package mx.utng.latp.smarthealthmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.utng.latp.smarthealthmonitor.data.models.LecturaFC
import mx.utng.latp.smarthealthmonitor.data.models.MockData
import mx.utng.latp.smarthealthmonitor.ui.components.FilaHistorial
import mx.utng.latp.smarthealthmonitor.ui.components.TarjetaDato
import mx.utng.latp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import mx.utng.latp.smarthealthmonitor.data.SmartHealthRepository
import mx.utng.latp.smarthealthmonitor.ui.viewmodel.DashboardViewModel
import mx.utng.latp.smarthealthmonitor.BuildConfig


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()  // ← inyección automática
) {
    // collectAsState() convierte StateFlow en State de Compose
    val fc     by viewModel.fc.collectAsState()
    val pasos  by viewModel.pasos.collectAsState()
    val historial = viewModel.historial

    // El resto del Composable no cambia.
    // fc y pasos ahora son reactivos: cuando el wearable
    // envía un dato, el Dashboard se actualiza automáticamente.


    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor    = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick       = onAlertClick,
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector       = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint              = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            // ⚠️ paddingValues OBLIGATORIO
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding        = PaddingValues(16.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)



            ) {
                // ── Tarjeta FC ────────────────────────────
                item {
                    TarjetaDato(
                        valor      = "$fc",
                        unidad     = "bpm",
                        label      = "Frecuencia cardíaca",
                        colorValor = MaterialTheme.colorScheme.error,
                        conectado  = fc > 0          // false → muestra "--" y "Esperando reloj…"
                    )
                }
                // ── Tarjeta Pasos ─────────────────────────
                item {
                    TarjetaDato(
                        valor      = "%,d".format(pasos),
                        unidad     = "pasos",
                        label      = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary,
                        conectado  = pasos > 0       // false → muestra "--" y "Esperando reloj…"
                    )
                }
                // ── Encabezado historial ──────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Historial reciente",
                            style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onHistorialClick) {
                            Text("Ver todo")
                        }
                    }
                }
                // ── Lista del historial ───────────────────
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }
                item {
                    // Botón de simulación — SOLO PARA DEBUG
                    if (BuildConfig.DEBUG) {
                        OutlinedButton(
                            onClick = {
                                // Simular lectura del wearable
                                val fcSimulado = (60..110).random()
                                SmartHealthRepository.actualizarFC(fcSimulado)
                                SmartHealthRepository.actualizarPasos((3000..8000).random())
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simular dato del wearable (DEBUG)")
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - Light",
    showSystemUi = true, device = "id:pixel_6")
@Preview(showBackground = true, name = "Dashboard - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenPreview() {
    SmartHealthMonitorTheme {
        DashboardScreen()
    }
}
