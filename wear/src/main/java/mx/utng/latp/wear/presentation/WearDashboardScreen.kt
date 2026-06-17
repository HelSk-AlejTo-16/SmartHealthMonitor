package mx.utng.latp.wear.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.scrollAway
import androidx.wear.compose.material3.Card

@Composable
fun WearDashboardScreen(
    onAlertClick:    () -> Unit = {},
    onHistorialClick: () -> Unit = {},   // ← NUEVO parámetro
    viewModel: WearDashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = {
            // La hora desaparece al hacer scroll
            TimeText(modifier = Modifier.scrollAway(listState))
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state    = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Item 1: Card de FC
            item {
                WearFCCard(
                    fc       = fc,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Item 2: Chip de Alerta
            item {
                Chip(
                    label   = { Text("⚠ Alerta") },
                    onClick  = onAlertClick,
                    colors   = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Item 3: Chip de Historial ← NUEVO
            item {
                Chip(
                    label    = { Text("📋 Historial") },
                    onClick  = onHistorialClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componente: Tarjeta de Frecuencia Cardíaca
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WearFCCard(
    fc: Int,
    modifier: Modifier = Modifier
) {
    Card(
        onClick   = {},
        modifier  = modifier
    ) {
        androidx.compose.foundation.layout.Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text  = "❤️ Frec. Cardíaca",
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurfaceVariant
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.padding(top = 4.dp)
            )
            androidx.compose.foundation.layout.Row(
                verticalAlignment     = androidx.compose.ui.Alignment.Bottom,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text       = "$fc",
                    fontSize   = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colors.error,
                    textAlign  = TextAlign.Center
                )
                Text(
                    text     = " bpm",
                    fontSize = 14.sp,
                    color    = MaterialTheme.colors.error,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}
