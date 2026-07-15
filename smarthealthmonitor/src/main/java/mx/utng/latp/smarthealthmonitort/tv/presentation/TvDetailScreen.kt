package mx.utng.latp.smarthealthmonitort.tv.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@Composable
fun TvDetailScreen(
    lecturaId: Int,
    navController: NavController,
    viewModel: TvViewModel = viewModel(factory = TvViewModelFactory(LocalContext.current))
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lectura = state.lecturas.find { it.id == lecturaId } ?: return

    // FocusRequester para mover el foco al primer botón al entrar
    val firstBtnFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstBtnFocus.requestFocus() }

    Row(
        Modifier.fillMaxSize().background(Color(0xFF0D1B4A)).padding(64.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {

        // Panel izquierdo — ícono + datos
        Column(Modifier.weight(0.4f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                Modifier.size(200.dp).background(Color(0xFF1565C0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("❤", fontSize = 80.sp)
            }
            Text(
                "${lectura.bpm} bpm",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White, 
                fontWeight = FontWeight.ExtraBold
            )
            
            val estadoTexto = lectura.estado
            Text(
                "Estado: $estadoTexto",
                style = MaterialTheme.typography.bodyLarge, 
                color = Color.White.copy(0.8f)
            )
            Text(
                "Hora: ${lectura.hora}",
                style = MaterialTheme.typography.bodyMedium, 
                color = Color.White.copy(0.6f)
            )
        }

        // Panel derecho — botones de acción
        Column(
            Modifier.weight(0.6f), verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(1f))

            // Botón Reproducir
            Surface(
                onClick = { navController.navigate("playback") },
                modifier = Modifier.focusRequester(firstBtnFocus)
                    .fillMaxWidth(0.7f).height(60.dp),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color(0xFF1B5E20),
                    focusedContainerColor = Color(0xFF76FF03)
                ),
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp))
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("▶  Reproducir", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón Volver
            Surface(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(0.7f).height(60.dp),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color(0xFF37474F),
                    focusedContainerColor = Color(0xFF90A4AE)
                ),
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp))
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("← Volver", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
