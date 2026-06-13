package mx.utng.latp.smarthealthmonitor.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.utng.latp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme

@Composable
fun TarjetaDato(
    valor: String,                        // "78" o "4,250"
    unidad: String,                       // "bpm" o "pasos"
    label: String,                        // "Frecuencia cardíaca"
    colorValor: Color,                    // MaterialTheme.colorScheme.error
    modifier: Modifier = Modifier,
    // true = hay dato real del reloj | false = esperando conexión (valor=0 inicial)
    conectado: Boolean = valor != "0" && valor != "0"
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // ── Encabezado: label + indicador de estado ──────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Indicador animado de estado de conexión
                if (conectado) {
                    ConexionDot(activo = true)
                } else {
                    Text(
                        text = "Esperando reloj…",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Valor principal ───────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    // Si no hay dato real, muestra "--" en lugar de "0"
                    text = if (conectado) valor else "--",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (conectado) colorValor
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Text(
                    text = unidad,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (conectado) colorValor
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

/** Punto verde pulsante que indica conexión activa con el reloj */
@Composable
private fun ConexionDot(activo: Boolean) {
    val pulso = rememberInfiniteTransition(label = "pulso")
    val escala by pulso.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )
    Box(
        modifier = Modifier
            .scale(escala)
            .size(8.dp)
            .background(Color(0xFF4CAF50), CircleShape)
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "TarjetaDato - Con datos")
@Composable
private fun TarjetaDatoConectadoPreview() {
    SmartHealthMonitorTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaDato(
                valor = "78", unidad = "bpm",
                label = "Frecuencia cardíaca",
                colorValor = MaterialTheme.colorScheme.error,
                conectado = true
            )
            TarjetaDato(
                valor = "4,250", unidad = "pasos",
                label = "Pasos del día",
                colorValor = MaterialTheme.colorScheme.primary,
                conectado = true
            )
        }
    }
}

@Preview(showBackground = true, name = "TarjetaDato - Sin conexión")
@Composable
private fun TarjetaDatoDesconectadoPreview() {
    SmartHealthMonitorTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaDato(
                valor = "0", unidad = "bpm",
                label = "Frecuencia cardíaca",
                colorValor = MaterialTheme.colorScheme.error,
                conectado = false
            )
            TarjetaDato(
                valor = "0", unidad = "pasos",
                label = "Pasos del día",
                colorValor = MaterialTheme.colorScheme.primary,
                conectado = false
            )
        }
    }
}
